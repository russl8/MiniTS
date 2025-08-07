package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.*;
import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.ExpressionProcessor;
import model.Expression.FunctionDeclaration;
import model.Expression.BlockContainer.BlockContainer;
import model.Expression.BlockContainer.ForLoop;
import model.Expression.BlockContainer.IfStatement;
import model.Expression.BlockContainer.WhileLoop;
import model.Expression.Expression.Type;
import model.Expression.Visitor.TypeChecker;
import model.Expression.Visitor.VariableBindingChecker;
import model.Expression.Visitor.OperationVisitor;
import model.Program.AntlrToProgram;
import model.Program.Program;
import model.MyErrorListener;
import org.antlr.v4.runtime.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Main class that parses, type-checks, and evaluates one or more input files,
 * then generates styled HTML reports.
 */
public class ExpressionApp {
	private static Map<String, Type> vars;
	private static Map<String, FunctionDeclaration> functions;
	private static List<String> semanticErrors;
	private static List<OperationVisitor> operationVisitors;

	// log setup
	private static final String LOG_FILE = "src/tests/history.log";
	private static PrintWriter logWriter;
	private static long sessionStartTime;

	public static void main(String[] args) {
		sessionStartTime = System.currentTimeMillis();
		setupLogging();
		logSessionStart(args);

		if (args.length != 1) {
			String errorMsg = "Error: missing folder or filename argument";
			logAndPrint(errorMsg, true);
			closeLogging();
			return;
		}

		File inputPath = new File(args[0]);
		if (!inputPath.exists()) {
			String errorMsg = "Error: The specified path does not exist.";
			logAndPrint(errorMsg, true);
			closeLogging();
			return;
		}

		File[] inputFiles = getInputFiles(inputPath);
		if (inputFiles == null || inputFiles.length == 0) {
			String errorMsg = "Error: No .txt files found in the specified path.";
			logAndPrint(errorMsg, true);
			closeLogging();
			return;
		}

		logMessage("Found " + inputFiles.length + " file(s) to process");

		List<String> individualReports = processFiles(inputFiles);

		if (!individualReports.isEmpty()) {
			String combinedReportPath = "src/tests/combined-report.html";
			writeCombinedHtmlReport(combinedReportPath, individualReports);
			String successMsg = "Generated combined report at " + combinedReportPath;
			logAndPrint(successMsg, false);
		}

		logSessionEnd();
		closeLogging();
	}

	private static void setupLogging() {
		try {
			File logFile = new File(LOG_FILE);
			ensureParentDirectoriesExist(logFile);
			logWriter = new PrintWriter(new FileWriter(LOG_FILE, true));
		} catch (IOException e) {
			System.err.println("Failed to setup logging: " + e.getMessage());
		}
	}

	private static void logMessage(String message) {
		if (logWriter != null) {
			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			logWriter.println("[" + timestamp + "] " + message);
			logWriter.flush();
		}
	}

	private static void logAndPrint(String message, boolean isError) {
		logMessage(message);
		if (isError) {
			System.err.println(message);
		} else {
			System.out.println(message);
		}
	}

	private static void logSessionStart(String[] args) {
		logMessage("===============================================================");
		logMessage("COMPILER SESSION STARTED");
		logMessage("Arguments: " + Arrays.toString(args));
		logMessage("Working Directory: " + System.getProperty("user.dir"));
		logMessage("Java Version: " + System.getProperty("java.version"));
		logMessage("===============================================================");
	}

	private static void logSessionEnd() {
		long sessionDuration = System.currentTimeMillis() - sessionStartTime;
		logMessage("===============================================================");
		logMessage("COMPILER SESSION COMPLETED");
		logMessage("Total Duration: " + String.format("%.2f seconds", sessionDuration / 1000.0));
		logMessage("===============================================================");
		logMessage("");
	}

	private static void closeLogging() {
		if (logWriter != null) {
			logWriter.close();
		}
	}

	private static File[] getInputFiles(File inputPath) {
		if (inputPath.isDirectory()) {
			logMessage("Processing directory: " + inputPath.getAbsolutePath());
			return inputPath.listFiles((dir, name) -> name.endsWith(".txt"));
		} else {
			logMessage("Processing single file: " + inputPath.getAbsolutePath());
			return new File[] { inputPath };
		}
	}

	private static List<String> processFiles(File[] inputFiles) {
		List<String> individualReports = new ArrayList<>();
		int successCount = 0;
		int failureCount = 0;

		for (int i = 0; i < inputFiles.length; i++) {
			File file = inputFiles[i];
			String filePath = file.getAbsolutePath();
			String processMsg = "Processing file " + (i + 1) + "/" + inputFiles.length + ": " + file.getName();
			logAndPrint(processMsg, false);

			ExprParser parser = getParser(filePath);
			if (parser == null || MyErrorListener.hasError) {
				String skipMsg = "WARNING: Skipping file due to syntax errors or parser failure.";
				logAndPrint(skipMsg, true);
				failureCount++;
				continue;
			}

			try {
				long startTime = System.currentTimeMillis();

				semanticErrors = new ArrayList<>();
				operationVisitors = new ArrayList<>();

				List<ClassDeclaration> classes = new ArrayList<>();

				AntlrToProgram progVisitor = new AntlrToProgram(semanticErrors, vars);
				Program prog = progVisitor.visit(parser.prog());

				TypeChecker typeCheckerVisitor = new TypeChecker(semanticErrors, vars, functions, classes);
				VariableBindingChecker varDeclarationCheckerVisitor = new VariableBindingChecker(semanticErrors, vars,
						functions, classes);

				operationVisitors.add(varDeclarationCheckerVisitor);
				operationVisitors.add(typeCheckerVisitor);

				for (Expression classExpr : prog.expressions) {
					ClassDeclaration cd = (ClassDeclaration) classExpr;

					functions = new HashMap<>();
					vars = new HashMap<>();

					// visit the class declaration
					visitExpression(cd, vars, functions, classes);

					// visit expressions in the class body
					for (Expression e : cd.expressions) {
						visitExpression(e, vars, functions, classes); // vars is the top-level global map
					}

					cd.functions = functions;
					cd.vars = vars;
					cd.semanticErrors.addAll(semanticErrors);
					semanticErrors.clear();
					classes.add(cd);
				}

				ExpressionProcessor ep = new ExpressionProcessor(functions, classes);
				for (ClassDeclaration cd : classes) {
					if (cd.semanticErrors.isEmpty()) {
						ep.vars = new HashMap<>();
						// evaluate the class declaration too
						ep.evaluateExpression(cd);

						for (Expression e : cd.expressions) {
							ep.evaluateExpression(e);
						}
						cd.setEvaluatedVars(ep.vars);
					}
				}

				
				logAnalysisResults(file.getName(), classes, getAllSemanticErrors(classes));
				
				// log class info for testing
				for (ClassDeclaration cd : classes) {
					String classInfo = "-------------------------" + cd.className + "-------------------------";
					logMessage(classInfo);
					logMessage("Functions: " + cd.functions);
					logMessage("Variables: " + cd.vars);
					logMessage("Evaluated Variables: " + cd.evaluatedVars);
				}
				
				
				// generate html report
				long processingTime = System.currentTimeMillis() - startTime;
				String reportPath = generateHtmlReport(file, filePath, ep, getAllSemanticErrors(classes),
						processingTime, classes);
				individualReports.add(reportPath);

				String reportMsg = "Generated report: " + reportPath;
				logAndPrint(reportMsg, false);

				logMessage("File processed in " + processingTime + "ms");
				successCount++;

			} catch (Exception e) {
				String errorMsg = "ERROR: Error processing file " + filePath + ": " + e.getMessage();
				logAndPrint(errorMsg, true);
				logMessage("Stack trace: " + getStackTraceAsString(e));
				failureCount++;
			}
		}

		logMessage("Processing Summary - Success: " + successCount + ", Failures: " + failureCount);
		return individualReports;
	}

	private static void logAnalysisResults(String fileName, List<ClassDeclaration> classes, List<String> errors) {
		logMessage("Analysis Results for: " + fileName);

		if (!errors.isEmpty()) {
			logMessage("Semantic Errors Found: " + errors.size());
			for (String error : errors) {
				logMessage("  - " + error);
			}
		} else {
			logMessage("No semantic errors found");
		}

		for (ClassDeclaration classDecl : classes) {
			logMessage("Class: " + classDecl.className);

			if (classDecl.functions != null && !classDecl.functions.isEmpty()) {
				logMessage("  Functions: " + classDecl.functions.size());
				for (String funcName : classDecl.functions.keySet()) {
					logMessage("    * " + funcName);
				}
			}

			if (classDecl.evaluatedVars != null && !classDecl.evaluatedVars.isEmpty()) {
				logMessage("  Variables: " + classDecl.evaluatedVars.size());
				for (Map.Entry<String, Value> entry : classDecl.evaluatedVars.entrySet()) {
					if (entry.getValue() != null) {
						String value = formatVariableValueForLog(entry.getValue());
						logMessage("    * " + entry.getKey() + ": " + entry.getValue().type.toString() + " = " + value);
					} else {
						logMessage("    * " + entry.getKey() + ": null = null");
					}
				}
			}
		}

		logMessage("Analysis Complete");
	}

	private static String formatVariableValueForLog(Value valueObj) {
		if (valueObj == null || valueObj.type == null) {
			return "null";
		}

		return switch (valueObj.type) {
		case INT -> String.valueOf(valueObj.getValueAsInt());
		case BOOL -> String.valueOf(valueObj.getValueAsBool());
		case CHAR -> "'" + valueObj.getValueAsCharacter() + "'";
		case LIST_CHAR, LIST_INT -> extractListValue(valueObj.toString());
		default -> "null";
		};
	}

	private static String extractListValue(String valueStr) {
		if (valueStr.contains("value=")) {
			int start = valueStr.indexOf("value=") + 6;
			int end = valueStr.indexOf("}", start);
			if (end == -1)
				end = valueStr.length();
			return valueStr.substring(start, end);
		}
		return valueStr;
	}

	private static String getStackTraceAsString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	private static void visitExpression(Expression e, Map<String, Type> currentVars,
			Map<String, FunctionDeclaration> functions, List<ClassDeclaration> classes) {

		if (!(e instanceof ForLoop)) {
			for (OperationVisitor v : operationVisitors) {
				v.updateVarState(currentVars);
				v.updateFunctionState(functions);
				e.accept(v);
			}
		}

		if (e instanceof BlockContainer) {
			Map<String, Type> savedVars = Utils.copyVarScope(currentVars);
			if (e instanceof ForLoop) {
				for (OperationVisitor v : operationVisitors) {
					v.updateVarState(currentVars);
					v.updateFunctionState(functions);
					e.accept(v);
				}
			}
			for (Expression ex : ((BlockContainer) e).getExpressions()) {
				visitExpression(ex, currentVars, functions, classes);
			}

			Utils.restoreVarScope(currentVars, savedVars);
		} else if (e instanceof FunctionDeclaration) {
			/*
			 * treat this as a regular expression, not a block container. only difference is
			 * that u dont visit the inner statements here. instead, do it in the visitors.
			 */
			FunctionDeclaration fd = ((FunctionDeclaration) e);
			if (functions.containsKey(fd.functionName)) {
				semanticErrors.add("Error at [" + fd.getLine() + ", " + fd.getCol() + "]: function " + fd.functionName
						+ " already declared");
			} else {
				functions.put(fd.functionName, fd);
			}
		}
	}

	private static String generateHtmlReport(File file, String filePath, ExpressionProcessor ep,
			List<String> semanticErrors, long processingTime, List<ClassDeclaration> classes) throws IOException {
		String baseName = file.getName().replace(".txt", "");
		String outputPath = "src/tests/" + baseName + "-report.html"; // Changed: removed output/ folder
		ensureParentDirectoriesExist(new File(outputPath));

		String inputFileContents = readFileContents(filePath);

		try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
			writer.println("<!DOCTYPE html>");
			writer.println("<html lang=\"en\">");
			writer.println("<head>");
			writer.println(generateHtmlHead(baseName + " Report"));
			writer.println("</head><body>");
			writer.println(generateHtmlHeader("Compilation Report"));
			writer.println("<div class=\"container\">");
			writer.println(generateLeftColumn(filePath, inputFileContents, semanticErrors));
			writer.println(
					generateRightColumn(ep.vars, semanticErrors, filePath, inputFileContents, processingTime, classes));
			writer.println("</div>");
			writer.println(generateFooter());
			writer.println("</body></html>");
		}

		return outputPath;
	}

	private static String readFileContents(String filePath) throws IOException {
		StringBuilder content = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = reader.readLine()) != null) {
				content.append(line).append("\n");
			}
		}
		return content.toString();
	}

	private static String generateHtmlHead(String title) {
		return String.format(
				"""
						<meta charset="UTF-8">
						<title>%s</title>
						<style>
						    body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; margin: 40px; background-color: #ede7e3; color: #16697a; }
						    h1 { color: #16697a; text-align: center; margin-bottom: 20px; }
						    .container { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; max-width: 100vw; }
						    .left, .right { padding: 20px; background-color: #ffffff; border: 1px solid #82c0cc; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
						    .left { max-width: 45vw; width: 45vw; overflow-x: auto; box-sizing: border-box; }
						    .right { max-width: 45vw; width: 45vw; overflow-x: auto; box-sizing: border-box; }
						    pre { background-color: #e9ecef; padding: 15px; border-radius: 8px; overflow-x: auto; font-size: 14px; font-family: 'Courier New', monospace; line-height: 1.4; width: 100%%; white-space: pre; box-sizing: border-box; }
						    .error { background-color: #f8d7da; border-left: 4px solid #d9534f; padding: 10px; margin-bottom: 10px; border-radius: 4px; color: #721c24; }
						    .variable { background-color: #489fb5; border-left: 4px solid #16697a; padding: 10px; margin-bottom: 10px; border-radius: 4px; color: #ffffff; }
						    .error-line { background-color: #f8d7da; color: #721c24; font-weight: bold; display: block; padding: 2px 5px; margin: 1px 0; border-radius: 3px; white-space: pre; }
						    .success-status { background-color: #d4edda; border-left: 4px solid #28a745; padding: 10px; margin-bottom: 15px; border-radius: 4px; color: #155724; font-weight: bold; }
						    .error-status { background-color: #f8d7da; border-left: 4px solid #dc3545; padding: 10px; margin-bottom: 15px; border-radius: 4px; color: #721c24; font-weight: bold; }
						    .stats { background-color: #f8f9fa; padding: 15px; border-radius: 4px; margin-bottom: 15px; border: 1px solid #dee2e6; }
						    .stat-item { margin-bottom: 8px; font-size: 14px; }
						    .variables-table { width: 100%%; border-collapse: collapse; margin-top: 10px; margin-bottom: 20px; background-color: #ffffff; border-radius: 4px; overflow: hidden; box-shadow: 0 2px 4px rgba(0,0,0,0.1); clear: both; table-layout: fixed; }
						    .variables-table th { background-color: #489fb5; color: white; padding: 12px 8px; text-align: left; font-weight: bold; border-bottom: 2px solid #16697a; width: 33.33%%; overflow: hidden; text-overflow: ellipsis; }
						    .variables-table td { padding: 10px 8px; border-bottom: 1px solid #e9ecef; width: 33.33%%; word-wrap: break-word; overflow: hidden; text-overflow: ellipsis; max-width: 0; }
						    .variables-table tbody tr:nth-child(even) { background-color: #f8f9fa; }
						    .variables-table tbody tr:hover { background-color: #e3f2fd; }
						    .table-container { margin-bottom: 25px; overflow-x: auto; clear: both; display: block; width: 100%%; }
						    .var-name { font-weight: bold; color: #16697a; }
						    .var-type { color: #6c757d; font-style: italic; }
						    .var-value { color: #28a745; font-family: 'Courier New', monospace; word-break: break-word; }
						    h2 { color: #489fb5; margin-top: 0; margin-bottom: 10px; }
						    h3 { color: #16697a; margin-top: 15px; margin-bottom: 8px; font-size: 18px; border-bottom: 2px solid #82c0cc; padding-bottom: 4px; }
						    h4 { color: #489fb5; margin-top: 15px; margin-bottom: 8px; font-size: 16px; }
						    footer { text-align: center; margin-top: 40px; font-size: 12px; color: #888; }
						    .report { margin-bottom: 30px; border-bottom: 2px solid #82c0cc; padding-bottom: 20px; }
						</style>
						""",
				title);
	}

	private static String generateHtmlHeader(String title) {
		return "<h1>" + escapeHTML(title) + "</h1>";
	}

	private static String generateLeftColumn(String filePath, String contents, List<String> errors) {
		Set<Integer> errorLines = extractErrorLineNumbers(errors);

		String[] lines = contents.split("\n");
		StringBuilder numberedContent = new StringBuilder();
		for (int i = 0; i < lines.length; i++) {
			int lineNumber = i + 1;
			if (errorLines.contains(lineNumber)) {
				numberedContent.append(String.format("<span class=\"error-line\">%3d | %s</span>\n", lineNumber,
						escapeHTML(lines[i])));
			} else {
				numberedContent.append(String.format("%3d | %s\n", lineNumber, escapeHTML(lines[i])));
			}
		}

		return String.format("""
				    <div class="left">
				        <h2>Input File Path</h2>
				        <pre><code>%s</code></pre>
				        <h2>Input File Contents</h2>
				        <pre><code>%s</code></pre>
				    </div>
				""", escapeHTML(filePath), numberedContent.toString());
	}

	private static Set<Integer> extractErrorLineNumbers(List<String> errors) {
		Set<Integer> errorLines = new HashSet<>();

		for (String error : errors) {
			extractLineNumber(error, "[", "]", errorLines);
			extractLineNumber(error, "line ", " ", errorLines);
			extractLineNumber(error, ": [", "]", errorLines);
			extractLineNumber(error, " at [", "]", errorLines);
			extractLineNumber(error, " in [", "]", errorLines);
		}

		return errorLines;
	}

	private static void extractLineNumber(String error, String startPattern, String endPattern,
			Set<Integer> errorLines) {
		if (!error.contains(startPattern))
			return;

		try {
			int start = error.indexOf(startPattern) + startPattern.length();
			int end = error.indexOf(endPattern, start);
			if (end == -1) {
				end = error.indexOf(" ", start);
				if (end == -1)
					end = error.indexOf(",", start);
				if (end == -1)
					end = error.length();
			}

			String lineStr = error.substring(start, end).trim();
			lineStr = lineStr.replaceAll("[^0-9]", "");

			if (!lineStr.isEmpty()) {
				int lineNumber = Integer.parseInt(lineStr);
				errorLines.add(lineNumber);
			}
		} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			// ignore if cant parse line #
		}
	}

	private static String generateRightColumn(Map<String, Value> values, List<String> errors, String filePath,
			String contents, long processingTime, List<ClassDeclaration> classes) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"right\">");

		boolean errorsExist = !errors.isEmpty();

		sb.append("<h2>Compilation Status</h2>");
		if (!errorsExist) {
			sb.append("<div class=\"success-status\">PASS : Compilation Successful</div>");
		} else {
			sb.append("<div class=\"error-status\">FAIL : >= 1 Errors</div>");
		}

		if (!classes.isEmpty()) {
			sb.append("<h2>Classes, Functions, and Variables</h2>");
			for (ClassDeclaration cd : classes) {
				sb.append("<h3>Class: ").append(escapeHTML(cd.className)).append("</h3>");

				if (cd.semanticErrors == null || cd.semanticErrors.isEmpty()) {
					// variable display
					if (cd.evaluatedVars != null && !cd.evaluatedVars.isEmpty()) {
						sb.append("<h4>Variables</h4>");
						sb.append("<div class=\"table-container\">");
						sb.append("<table class=\"variables-table\">");
						sb.append("<thead><tr><th>Variable Name</th><th>Type</th><th>Value</th></tr></thead>");
						sb.append("<tbody>");
						for (Map.Entry<String, Value> entry : cd.evaluatedVars.entrySet()) {
							String variableName = escapeHTML(entry.getKey());
							Value valueObj = entry.getValue();

							if (valueObj == null || valueObj.type == null) {
								sb.append("<tr>");
								sb.append("<td class=\"var-name\">").append(variableName).append("</td>");
								sb.append("<td class=\"var-type\">unknown</td>");
								sb.append("<td class=\"var-value\">null</td>");
								sb.append("</tr>");
								continue;
							}

							String type = valueObj.type.toString();
							String actualValue = formatVariableValueForLog(valueObj);

							sb.append("<tr>");
							sb.append("<td class=\"var-name\">").append(variableName).append("</td>");
							sb.append("<td class=\"var-type\">").append(escapeHTML(type)).append("</td>");
							sb.append("<td class=\"var-value\">").append(escapeHTML(actualValue)).append("</td>");
							sb.append("</tr>");
						}
						sb.append("</tbody>");
						sb.append("</table>");
						sb.append("</div>");
					}

					// function display
					if (cd.functions != null && !cd.functions.isEmpty()) {
						sb.append("<h4>Functions</h4>");
						sb.append("<div class=\"table-container\">");
						sb.append("<table class=\"variables-table\">");
						sb.append(
								"<thead><tr><th>Function Name</th><th>Return Type</th><th>Parameters</th></tr></thead>");
						sb.append("<tbody>");
						for (Map.Entry<String, FunctionDeclaration> entry : cd.functions.entrySet()) {
							String functionName = escapeHTML(entry.getKey());
							FunctionDeclaration func = entry.getValue();
							String returnType = func.returnType != null ? escapeHTML(func.returnType.toString())
									: "void";

							// Build parameters string
							StringBuilder params = new StringBuilder();
							if (func.parameters != null && !func.parameters.isEmpty()) {
								for (int i = 0; i < func.parameters.size(); i++) {
									if (i > 0)
										params.append(", ");
									params.append(func.parameters.get(i).toString());
								}
							} else {
								params.append("()");
							}

							sb.append("<tr>");
							sb.append("<td class=\"var-name\">").append(functionName).append("</td>");
							sb.append("<td class=\"var-type\">").append(returnType).append("</td>");
							sb.append("<td class=\"var-value\">").append(escapeHTML(params.toString())).append("</td>");
							sb.append("</tr>");
						}
						sb.append("</tbody>");
						sb.append("</table>");
						sb.append("</div>");
					}
				}
			}
		}

		// show metrics
		appendCodeMetrics(sb, contents, classes);

		if (!errors.isEmpty()) {
			sb.append("<h2>Error Details</h2>");
			for (String err : errors) {
				sb.append("<div class=\"error\">").append(escapeHTML(err)).append("</div>");
			}
		}

		sb.append("</div>");
		return sb.toString();
	}

	private static void appendCodeMetrics(StringBuilder sb, String contents, List<ClassDeclaration> classes) {
		sb.append("<h2>Code Metrics</h2>");
		sb.append("<div class=\"stats\">");

		String[] lines = contents.split("\n");
		int totalLines = lines.length;
		int nonEmptyLines = 0;
		int ifStatements = 0;
		int forLoops = 0;
		int whileLoops = 0;
		int commentLines = 0;
		// count types of lines and statements
		for (String line : lines) {
			String trimmedLine = line.trim();
			if (!trimmedLine.isEmpty()) {
				nonEmptyLines++;

				if (trimmedLine.startsWith("//")) {
					commentLines++;
				}

				if (trimmedLine.contains("if(") || trimmedLine.contains("if (")) {
					ifStatements++;
				}

				if (trimmedLine.contains("for(") || trimmedLine.contains("for (")) {
					forLoops++;
				}

				if (trimmedLine.contains("while(") || trimmedLine.contains("while (")) {
					whileLoops++;
				}
			}
		}

		// calculate class and function metrics
		int totalClasses = classes.size();
		int totalFunctions = 0;
		int totalVariables = 0;

		for (ClassDeclaration classDecl : classes) {
			if (classDecl.evaluatedVars != null) {
				totalVariables += classDecl.evaluatedVars.size();
			}
			if (classDecl.functions != null) {
				totalFunctions += classDecl.functions.size();
			}
		}

		appendMetric(sb, "Total Lines", totalLines);
		appendMetric(sb, "Non-Empty Lines", nonEmptyLines);
		appendMetric(sb, "Classes Declared", totalClasses);
		appendMetric(sb, "Functions Declared", totalFunctions);
		appendMetric(sb, "Variables Declared", totalVariables);
		appendMetric(sb, "If Statements", ifStatements);
		appendMetric(sb, "For Loops", forLoops);
		appendMetric(sb, "While Loops", whileLoops);
		appendMetric(sb, "Errors Found", getAllSemanticErrors(classes).size());

		sb.append("</div>");
	}

	private static void appendMetric(StringBuilder sb, String label, Object value) {
		sb.append("<div class=\"stat-item\">").append(label).append(": <strong>").append(value)
				.append("</strong></div>");
	}

	private static void writeCombinedHtmlReport(String outputPath, List<String> reportPaths) {
		ensureParentDirectoriesExist(new File(outputPath));

		try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
			writer.println("<!DOCTYPE html><html><head>");
			writer.println(generateHtmlHead("Combined Report"));
			writer.println("</head><body>");
			writer.println("<h1>Combined Compilation Report</h1>");

			for (String reportPath : reportPaths) {
				String fileName = new File(reportPath).getName();
				writer.println("<div class=\"report\">");
				writer.println("<h2><a href=\"" + escapeHTML(fileName) + "\">" + escapeHTML(fileName) + "</a></h2>");

				try (BufferedReader reader = new BufferedReader(new FileReader(reportPath))) {
					String line;
					while ((line = reader.readLine()) != null) {
						if (shouldSkipLine(line)) {
							continue;
						}
						writer.println(line);
					}
				} catch (IOException e) {
					writer.println("<p>Error reading report: " + escapeHTML(fileName) + "</p>");
				}

				writer.println("</div>");
			}

			writer.println(generateFooter());
			writer.println("</body></html>");
		} catch (IOException e) {
			String errorMsg = "Error writing combined report: " + e.getMessage();
			logMessage(errorMsg);
			System.err.println(errorMsg);
		}
	}

	private static boolean shouldSkipLine(String line) {
		return (line.contains("<h1>") && line.contains("Compilation Report")) || line.contains("<footer>");
	}

	private static void ensureParentDirectoriesExist(File file) {
		File parentDir = file.getParentFile();
		if (parentDir != null && !parentDir.exists()) {
			parentDir.mkdirs();
		}
	}

	private static String escapeHTML(String input) {
		return input == null ? ""
				: input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
	}

	private static String generateFooter() {
		return "<footer>team: russl8, jeffj4</footer>";
	}

	private static ExprParser getParser(String fileName) {
		try {
			CharStream input = CharStreams.fromFileName(fileName);
			ExprLexer lexer = new ExprLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			ExprParser parser = new ExprParser(tokens);
			parser.removeErrorListeners();
			parser.addErrorListener(new MyErrorListener());
			return parser;
		} catch (IOException e) {
			System.err.println("Error reading file: " + fileName + " - " + e.getMessage());
			return null;
		}
	}

	private static List<String> getAllSemanticErrors(List<ClassDeclaration> classes) {
		List<String> allErrors = new ArrayList<>();
		for (ClassDeclaration cd : classes) {
			if (cd.semanticErrors != null) {
				allErrors.addAll(cd.semanticErrors);
			}
		}
		return allErrors;
	}
}