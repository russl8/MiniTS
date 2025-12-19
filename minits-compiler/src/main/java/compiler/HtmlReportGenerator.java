package compiler;

import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.FunctionDeclaration;
import model.Value;

import java.io.*;
import java.util.*;

public class HtmlReportGenerator {
    public static String generate(File file, String filePath,
                                  List<String> semanticErrors, List<ClassDeclaration> classes) throws IOException {
        String baseName = file.getName().replace(".txt", "");
        String outputPath = "src/tests/" + baseName + "-report.html";
        ensureParentDirectoriesExist(new File(outputPath));

        List<String> allSemanticErrors = new ArrayList<>();
        for (ClassDeclaration cd : classes)
            allSemanticErrors.addAll(cd.semanticErrors);

        String inputFileContents = readFileContents(filePath);

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputPath))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html lang=\"en\">");
            writer.println("<head>");
            writer.println(generateHtmlHead(baseName + " Report"));
            writer.println("</head><body>");
            writer.println(generateHtmlHeader("Compilation Report"));
            writer.println("<div class=\"container\">");
            writer.println(generateLeftColumn(filePath, inputFileContents, allSemanticErrors));
            writer.println(
                    generateRightColumn(semanticErrors, filePath, inputFileContents, classes));
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
        Set<Integer> errorLines = new HashSet<>();
        for (String error : errors) {
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
                }
            }
            // look for patterns like "at [11, 13]" in error messages (type mismatches)
            if (error.contains(" at [")) {
                try {
                    int start = error.indexOf(" at [") + 5;
                    int end = error.indexOf(",", start);
                    if (end != -1) {
                        String lineStr = error.substring(start, end);
                        errorLines.add(Integer.parseInt(lineStr));
                    }
                } catch (NumberFormatException e) {
                }
            }

            if (error.contains(" in [")) {
                try {
                    int start = error.indexOf(" in [") + 5;
                    int end = error.indexOf(",", start);
                    if (end != -1) {
                        String lineStr = error.substring(start, end);
                        errorLines.add(Integer.parseInt(lineStr));
                    }
                } catch (NumberFormatException e) {
                }
            }
        }

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

    private static String generateRightColumn(List<String> errors, String filePath,
                                              String contents, List<ClassDeclaration> classes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"right\">");

        boolean errorsExist = false;
        for (ClassDeclaration cd : classes) {
            if (!cd.semanticErrors.isEmpty()) {
                errorsExist = true;
                break;
            }
        }


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

                if (cd.semanticErrors.isEmpty()) {
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
                            Expression.Type valueObjType = valueObj == null ? null : valueObj.type;
                            if (valueObj == null)
                                continue;
                            String actualValue = "null";
                            if (valueObjType == Expression.Type.INT) {
                                actualValue = String.valueOf(valueObj.getValueAsInt());
                            } else if (valueObjType == Expression.Type.BOOL) {
                                actualValue = String.valueOf(valueObj.getValueAsBool());
                            } else if (valueObjType == Expression.Type.CHAR) {
                                actualValue = "'" + valueObj.getValueAsCharacter() + "'";
                            } else if (valueObjType == Expression.Type.LIST_CHAR || valueObjType == Expression.Type.LIST_INT) {
                                String valueStr = valueObj.toString();
                                if (valueStr.contains("value=")) {
                                    int start = valueStr.indexOf("value=") + 6;
                                    int end = valueStr.indexOf("}", start);
                                    if (end == -1)
                                        end = valueStr.length();
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

                } else {
                    sb.append("<h2>Error Details</h2>");
                    for (String err : cd.semanticErrors) {
                        sb.append("<div class=\"error\">").append(escapeHTML(err)).append("</div>");
                    }

                }

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
                if (trimmed.contains("if(") || trimmed.contains("if (")) {
                    ifStatements++;
                }
                if (trimmed.contains("while(") || trimmed.contains("while (")) {
                    whileLoops++;
                }
            }
        }
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

        sb.append("</div>");
        return sb.toString();
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
        return "<footer>russl8</footer>";
    }
}
