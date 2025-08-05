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

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Error: missing folder or filename argument");
			return;
		}

		File inputPath = new File(args[0]);
		if (!inputPath.exists()) {
			System.err.println("Error: The specified path does not exist.");
			return;
		}

		File[] inputFiles = getInputFiles(inputPath);
		if (inputFiles == null || inputFiles.length == 0) {
			System.err.println("Error: No .txt files found in the specified path.");
			return;
		}

		List<String> individualReports = processFiles(inputFiles);

		if (!individualReports.isEmpty()) {
			String combinedReportPath = "src/tests/output/combined-report.html";
			writeCombinedHtmlReport(combinedReportPath, individualReports);
			System.out.println("Generated combined report at " + combinedReportPath);
		}
	}

	private static File[] getInputFiles(File inputPath) {
		if (inputPath.isDirectory()) {
			return inputPath.listFiles((dir, name) -> name.endsWith(".txt"));
		} else {
			return new File[] { inputPath };
		}
	}

	private static List<String> processFiles(File[] inputFiles) {
		List<String> individualReports = new ArrayList<>();

		for (File file : inputFiles) {
			String filePath = file.getAbsolutePath();
			System.out.println("Processing file: " + filePath);

			ExprParser parser = getParser(filePath);
			if (parser == null || MyErrorListener.hasError) {
				System.err.println("Skipping file due to syntax errors or parser failure.");
				continue;
			}

			try {
				long startTime = System.currentTimeMillis();

				semanticErrors = new ArrayList<>();
				operationVisitors = new ArrayList<>();

				// Create a list of class declarations
				List<ClassDeclaration> classes = new ArrayList<>();

				AntlrToProgram progVisitor = new AntlrToProgram(semanticErrors, vars);
				Program prog = progVisitor.visit(parser.prog());

				TypeChecker typeCheckerVisitor = new TypeChecker(semanticErrors, vars, functions,
						classes);
				VariableBindingChecker varDeclarationCheckerVisitor = new VariableBindingChecker(
						semanticErrors, vars, functions, classes);

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

					classes.add(cd);
				}

				ExpressionProcessor ep = new ExpressionProcessor(functions, classes);
				if (semanticErrors.isEmpty()) {
					for (ClassDeclaration cd : classes) {
						ep.vars = new HashMap<>();
						// evaluate the class declaration too
						ep.evaluateExpression(cd);

						for (Expression e : cd.expressions) {
							ep.evaluateExpression(e);
						}
						cd.setEvaluatedVars(ep.vars);
					}
				}

				/**
				 * 
				 * Usage of List<ClassDeclaration> classes:
				 * 
				 * for (ClassDeclaration cd : classes) { //cd.functions returns a list of all
				 * functions declared in the class //cd.evaluatedVar returns a list of all
				 * variables in the class alongside their values }
				 */

				for (ClassDeclaration cd : classes) {
					// cd.functions returns a list of all functions declared in the class
					// cd.evaluatedVar returns a list of all variables in the class and their
					// evaluated values
					// cd.vars returns a list of all variables with their corresponding types
					System.out.println("-------------------------" + cd.className + "-------------------------");
					System.out.println(cd.functions);
					System.out.println(cd.vars);
					System.out.println(cd.evaluatedVars);
				}

				// TODO: refactor below functions to support multiple classes using the classes
				// list
				long processingTime = System.currentTimeMillis() - startTime;
				String reportPath = generateHtmlReport(file, filePath, ep, semanticErrors, processingTime, classes);
				individualReports.add(reportPath);
			} catch (Exception e) {
				System.err.println("Error processing file " + filePath + ": " + e.getMessage());
				e.printStackTrace();
			}
		}

		return individualReports;
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
				semanticErrors.add("Function " + fd.functionName + " already declared: [" + fd.getLine() + "]");
			} else {
				functions.put(fd.functionName, fd);
			}

		}
	}

	private static String generateHtmlReport(File file, String filePath, ExpressionProcessor ep,
			List<String> semanticErrors, long processingTime, List<ClassDeclaration> classes) throws IOException {
		String baseName = file.getName().replace(".txt", "");
		String outputPath = "src/tests/output/" + baseName + "-report.html";
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

		System.out.println("Generated report: " + outputPath);
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
		// Extract error line numbers from error messages
		Set<Integer> errorLines = new HashSet<>();
		for (String error : errors) {
			// Look for patterns like "line=5" or "line 5" in error messages
			if (error.contains("line=")) {
				try {
					int start = error.indexOf("line=") + 5;
					int end = error.indexOf(" ", start);
					if (end == -1)
						end = error.indexOf(",", start);
					if (end == -1)
						end = error.length();
					String lineStr = error.substring(start, end);
					errorLines.add(Integer.parseInt(lineStr));
				} catch (NumberFormatException e) {
					// Ignore if we can't parse the line number
				}
			}
			// Look for patterns like "at [11, 13]" in error messages (type mismatches)
			if (error.contains(" at [")) {
				try {
					int start = error.indexOf(" at [") + 5;
					int end = error.indexOf(",", start);
					if (end != -1) {
						String lineStr = error.substring(start, end);
						errorLines.add(Integer.parseInt(lineStr));
					}
				} catch (NumberFormatException e) {
					// Ignore if we can't parse the line number
				}
			}
			// Look for patterns like "in [11, 13]" in error messages (assignment to
			// undeclared variables)
			if (error.contains(" in [")) {
				try {
					int start = error.indexOf(" in [") + 5;
					int end = error.indexOf(",", start);
					if (end != -1) {
						String lineStr = error.substring(start, end);
						errorLines.add(Integer.parseInt(lineStr));
					}
				} catch (NumberFormatException e) {
					// Ignore if we can't parse the line number
				}
			}
		}

		// Add line numbers to the content with error highlighting
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

	private static String generateRightColumn(Map<String, Value> values, List<String> errors, String filePath,
			String contents, long processingTime, List<ClassDeclaration> classes) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"right\">");

		// Always show compilation status first
		sb.append("<h2>Compilation Status</h2>");
		if (errors.isEmpty()) {
			sb.append("<div class=\"success-status\">PASS - Compilation Successful</div>");
		} else {
			sb.append("<div class=\"error-status\">FAIL - Compilation Failed</div>");
		}

		// Only show classes information if there are no errors and we have classes
		if (errors.isEmpty() && !classes.isEmpty()) {
			sb.append("<h2>Classes, Functions, and Variables</h2>");
			for (ClassDeclaration cd : classes) {
				sb.append("<h3>Class: ").append(escapeHTML(cd.className)).append("</h3>");

				// Display variables table for this class
				if (cd.evaluatedVars != null && !cd.evaluatedVars.isEmpty()) {
					sb.append("<h4>Variables</h4>");
					sb.append("<div class=\"table-container\">");
					sb.append("<table class=\"variables-table\">");
					sb.append("<thead><tr><th>Variable Name</th><th>Type</th><th>Value</th></tr></thead>");
					sb.append("<tbody>");
					for (Map.Entry<String, Value> entry : cd.evaluatedVars.entrySet()) {
                        String variableName = escapeHTML(entry.getKey());
                        Value valueObj = entry.getValue();
                        String type = valueObj.type.toString();
                        
                        // Get just the actual value, not the full toString representation
                        String actualValue = "null";
                        if (valueObj.type == Type.INT) {
                            actualValue = String.valueOf(valueObj.getValueAsInt());
                        } else if (valueObj.type == Type.BOOL) {
                            actualValue = String.valueOf(valueObj.getValueAsBool());
                        } else if (valueObj.type == Type.CHAR) {
                            actualValue = "'" + valueObj.getValueAsCharacter() + "'";
                        } else if (valueObj.type == Type.LIST_CHAR || valueObj.type == Type.LIST_INT) {
                            // Just extract the value=[...] part from toString()
                            String valueStr = valueObj.toString();
                            if (valueStr.contains("value=")) {
                                int start = valueStr.indexOf("value=") + 6;
                                int end = valueStr.indexOf("}", start);
                                if (end == -1) end = valueStr.length();
                                actualValue = valueStr.substring(start, end);
                            }
                        }
                        
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

				// Display functions for this class
				if (cd.functions != null && !cd.functions.isEmpty()) {
					sb.append("<h4>Functions</h4>");
					sb.append("<div class=\"table-container\">");
					sb.append("<table class=\"variables-table\">");
					sb.append("<thead><tr><th>Function Name</th><th>Return Type</th><th>Parameters</th></tr></thead>");
					sb.append("<tbody>");
					for (Map.Entry<String, FunctionDeclaration> entry : cd.functions.entrySet()) {
						String functionName = escapeHTML(entry.getKey());
						FunctionDeclaration func = entry.getValue();
						String returnType = func.returnType != null ? escapeHTML(func.returnType.toString()) : "void";

						// Build parameters string
						StringBuilder params = new StringBuilder();
						if (func.parameters != null && !func.parameters.isEmpty()) {
							for (int i = 0; i < func.parameters.size(); i++) {
								if (i > 0)
									params.append(", ");
								// Assuming parameters have name and type - adjust based on actual structure
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

		// Show code metrics
		sb.append("<h2>Code Metrics</h2>");
		sb.append("<div class=\"stats\">");
		String[] lines = contents.split("\n");
		int nonEmptyLines = 0;
		int commentLines = 0;
		int ifStatements = 0;
		int whileLoops = 0;

		for (String line : lines) {
			String trimmed = line.trim();
			if (!trimmed.isEmpty()) {
				nonEmptyLines++;
				if (trimmed.startsWith("//")) {
					commentLines++;
				}
				// Count if statements
				if (trimmed.contains("if(") || trimmed.contains("if (")) {
					ifStatements++;
				}
				// Count while loops
				if (trimmed.contains("while(") || trimmed.contains("while (")) {
					whileLoops++;
				}
			}
		}
		// Calculate total variables across all classes
		int totalVariables = 0;
		for (ClassDeclaration cd : classes) {
			if (cd.evaluatedVars != null) {
				totalVariables += cd.evaluatedVars.size();
			}
		}

		sb.append("<div class=\"stat-item\">Total Lines: <strong>").append(lines.length).append("</strong></div>");
		sb.append("<div class=\"stat-item\">Non-Empty Lines: <strong>").append(nonEmptyLines).append("</strong></div>");
		sb.append("<div class=\"stat-item\">Comment Lines: <strong>").append(commentLines).append("</strong></div>");
		sb.append("<div class=\"stat-item\">If Statements: <strong>").append(ifStatements).append("</strong></div>");
		sb.append("<div class=\"stat-item\">While Loops: <strong>").append(whileLoops).append("</strong></div>");
		sb.append("<div class=\"stat-item\">Variables Declared: <strong>").append(totalVariables)
				.append("</strong></div>");
		sb.append("<div class=\"stat-item\">Errors Found: <strong>").append(errors.size()).append("</strong></div>");
		sb.append("</div>");

		// Only show errors if there are errors
		if (!errors.isEmpty()) {
			sb.append("<h2>Error Details</h2>");
			for (String err : errors) {
				sb.append("<div class=\"error\">").append(escapeHTML(err)).append("</div>");
			}
		}

		sb.append("</div>");
		return sb.toString();
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
						// Skip the individual "Compilation Report" h1 header and footer
						if ((line.contains("<h1>") && line.contains("Compilation Report"))
								|| line.contains("<footer>")) {
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
			System.err.println("Error writing combined report: " + e.getMessage());
		}
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
}
