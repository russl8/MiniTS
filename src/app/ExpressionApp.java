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
    
    // Logging setup
    private static final String LOG_FILE = "src/tests/history.log";  // Changed from src/tests/output/history.log
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
            String combinedReportPath = "src/tests/combined-report.html";  // Already correct
            writeCombinedHtmlReport(combinedReportPath, individualReports);
            String successMsg = "Generated combined report at " + combinedReportPath;
            logAndPrint(successMsg, false);
        }

        logSessionEnd();
        closeLogging();
    }

    // Logging methods - now LOG_FILE points to src/tests/history.log
    private static void setupLogging() {
        try {
            File logFile = new File(LOG_FILE);  // LOG_FILE now points to src/tests/history.log
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
        logMessage("EXECUTION STARTED");
        logMessage("Arguments: " + Arrays.toString(args));
        logMessage("Working Directory: " + System.getProperty("user.dir"));
        logMessage("Java Version: " + System.getProperty("java.version"));
        logMessage("===============================================================");
    }

    private static void logSessionEnd() {
        long sessionDuration = System.currentTimeMillis() - sessionStartTime;
        logMessage("===============================================================");
        logMessage("EXECUTION COMPLETED");
        logMessage("===============================================================");
        logMessage(""); // Empty line for separation between sessions
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

                // Create a list of class declarations
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

                // Log analysis results with the enhanced logging we added
                logAnalysisResults(file.getName(), classes, getAllSemanticErrors(classes));

                // Console output (keep original for backwards compatibility)
                for (ClassDeclaration cd : classes) {
                    String classInfo = "-------------------------" + cd.className + "-------------------------";
                    logAndPrint(classInfo, false);
                    logAndPrint("Functions: " + cd.functions, false);
                    logAndPrint("Variables: " + cd.vars, false);
                    logAndPrint("Evaluated Variables: " + cd.evaluatedVars, false);
                }

                // Generate report
                long processingTime = System.currentTimeMillis() - startTime;
                String reportPath = generateHtmlReport(file, filePath, ep, getAllSemanticErrors(classes), processingTime, classes);
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

    // Add this helper method to collect all semantic errors from classes
    private static List<String> getAllSemanticErrors(List<ClassDeclaration> classes) {
        List<String> allErrors = new ArrayList<>();
        for (ClassDeclaration cd : classes) {
            if (cd.semanticErrors != null) {
                allErrors.addAll(cd.semanticErrors);
            }
        }
        return allErrors;
    }

    // Add the missing logging methods we discussed earlier
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
                    String value = formatVariableValueForLog(entry.getValue());
                    logMessage("    * " + entry.getKey() + ": " + entry.getValue().type.toString() + " = " + value);
                }
            }
        }
        
        logMessage("Analysis Complete");
    }

    private static String formatVariableValueForLog(Value valueObj) {
        return switch (valueObj.type) {
            case INT -> String.valueOf(valueObj.getValueAsInt());
            case BOOL -> String.valueOf(valueObj.getValueAsBool());
            case CHAR -> "'" + valueObj.getValueAsCharacter() + "'";
            case LIST_CHAR, LIST_INT -> extractListValue(valueObj.toString());
            default -> "null";
        };
    }

    private static String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    // Update generateRightColumn to use the errors parameter consistently
    private static String generateRightColumn(Map<String, Value> values, List<String> errors, String filePath,
            String contents, long processingTime, List<ClassDeclaration> classes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"right\">");

        boolean errorsExist = !errors.isEmpty();

        // Always show compilation status first
        sb.append("<h2>Compilation Status</h2>");
        if (!errorsExist) {
            sb.append("<div class=\"success-status\">PASS : Compilation Successful</div>");
        } else {
            sb.append("<div class=\"error-status\">FAIL : >= 1 Errors</div>");
        }

        // Only show classes information if there are no errors and we have classes
        if (!classes.isEmpty()) {
            sb.append("<h2>Classes, Functions, and Variables</h2>");
            for (ClassDeclaration cd : classes) {
                sb.append("<h3>Class: ").append(escapeHTML(cd.className)).append("</h3>");

                if (cd.semanticErrors == null || cd.semanticErrors.isEmpty()) {
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

                            String type = valueObj == null ? null : valueObj.type.toString();
                            Type valueObjType = valueObj == null ? null : valueObj.type;
                            if (valueObj == null)
                                continue;
                            
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

        // Show code metrics using the existing appendCodeMetrics method
        appendCodeMetrics(sb, contents, classes);

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

	private static void appendCodeMetrics(StringBuilder sb, String contents, List<ClassDeclaration> classes) {
        sb.append("<h2>Code Metrics</h2>");
        sb.append("<div class=\"stats\">");

        // Analyze source code lines
        String[] lines = contents.split("\n");
        int totalLines = lines.length;
        int nonEmptyLines = 0;
        int commentLines = 0;
        int ifStatements = 0;
        int forLoops = 0;        // Added for loops
        int whileLoops = 0;

        // Count different types of lines and statements
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
                
                // Added for loop detection
                if (trimmedLine.contains("for(") || trimmedLine.contains("for (")) {
                    forLoops++;
                }
                
                if (trimmedLine.contains("while(") || trimmedLine.contains("while (")) {
                    whileLoops++;
                }
            }
        }
        
        // Calculate class and function metrics from parsed data
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
        
        // Calculate meaningful averages
        double avgFunctionsPerClass = totalClasses > 0 ? (double) totalFunctions / totalClasses : 0.0;
        double avgVariablesPerClass = totalClasses > 0 ? (double) totalVariables / totalClasses : 0.0;

        // Display metrics in the order you specified
        appendMetric(sb, "Total Lines", totalLines);
        appendMetric(sb, "Non-Empty Lines", nonEmptyLines);
        appendMetric(sb, "Classes Declared", totalClasses);
        appendMetric(sb, "Functions Declared", totalFunctions);
        appendMetric(sb, "Variables Declared", totalVariables);
        appendMetric(sb, "If Statements", ifStatements);
        appendMetric(sb, "For Loops", forLoops);           // Added for loops metric
        appendMetric(sb, "While Loops", whileLoops);
        appendMetric(sb, "Avg Functions per Class", String.format("%.1f", avgFunctionsPerClass));
        appendMetric(sb, "Avg Variables per Class", String.format("%.1f", avgVariablesPerClass));
        appendMetric(sb, "Errors Found", semanticErrors.size());

        sb.append("</div>");
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
                        // Skip individual headers and footers
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

	private static void appendMetric(StringBuilder sb, String label, Object value) {
        sb.append("<div class=\"stat-item\">")
          .append(label)
          .append(": <strong>")
          .append(value)
          .append("</strong></div>");
    }

    private static String extractListValue(String valueStr) {
        if (valueStr.contains("value=")) {
            int start = valueStr.indexOf("value=") + 6;
            int end = valueStr.indexOf("}", start);
            if (end == -1) end = valueStr.length();
            return valueStr.substring(start, end);
        }
        return valueStr;
    }

    private static boolean shouldSkipLine(String line) {
        return (line.contains("<h1>") && line.contains("Compilation Report")) || 
               line.contains("<footer>");
    }
}
