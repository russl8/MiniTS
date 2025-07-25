package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.*;
import model.Expression.Expression;
import model.Expression.ExpressionProcessor;
import model.Expression.Declaration.ClassDeclaration;
import model.Expression.Expression.Type;
import model.Expression.OperationVisitor.ExpressionTypeChecker;
import model.Expression.OperationVisitor.ExpressionVariableDeclarationChecker;
import model.Expression.OperationVisitor.OperationVisitor;
import model.Expression.Statement.IfStatement;
import model.Expression.Statement.WhileLoop;
import model.Program.AntlrToProgram;
import model.Program.Program;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.*;
import java.util.*;

/**
 * Main class that parses, type-checks, and evaluates one or more input files,
 * then generates styled HTML reports.
 */
public class ExpressionApp {
	private static Map<String, Type> vars;
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
				semanticErrors = new ArrayList<>();
				vars = new HashMap<>();
				operationVisitors = new ArrayList<>();

				ExpressionProcessor ep = new ExpressionProcessor();
				AntlrToProgram progVisitor = new AntlrToProgram(semanticErrors, vars);
				Program prog = progVisitor.visit(parser.prog());

				ExpressionTypeChecker typeCheckerVisitor = new ExpressionTypeChecker(semanticErrors, vars);
				ExpressionVariableDeclarationChecker varDeclarationCheckerVisitor = new ExpressionVariableDeclarationChecker(
						semanticErrors, vars);

				operationVisitors.add(varDeclarationCheckerVisitor);
				operationVisitors.add(typeCheckerVisitor);

				for (Expression classExpr : prog.expressions) {
					// future: class scoping
					ClassDeclaration cd = (ClassDeclaration) classExpr;
					for (Expression e : cd.expressions) {
						visitExpression(e, vars); // vars is the top-level global map
					}
				}

				if (semanticErrors.isEmpty()) {
					for (Expression classExpr : prog.expressions) {
						ClassDeclaration cd = (ClassDeclaration) classExpr;
						for (Expression e : cd.expressions) {
							ep.evaluateExpression(e);
						}
					}
				}

				String reportPath = generateHtmlReport(file, filePath, ep, semanticErrors);
				individualReports.add(reportPath);
			} catch (Exception e) {
				System.err.println("Error processing file " + filePath + ": " + e.getMessage());
				e.printStackTrace();
			}
		}

		return individualReports;
	}

	private static void visitExpression(Expression e, Map<String, Type> currentVars) {

		for (OperationVisitor v : operationVisitors) {
			v.updateVarState(currentVars);
			e.accept(v);
		}

		if (e instanceof IfStatement) {
			Map<String, Type> savedVars = new HashMap<>(currentVars);
			Map<String, Type> blockVars = new HashMap<>(currentVars);

			IfStatement ifs = (IfStatement) e;
			for (Expression ex : ifs.expressions) {
				visitExpression(ex, blockVars);
			}

			currentVars.clear();
			currentVars.putAll(savedVars);
		} else if (e instanceof WhileLoop) {
			Map<String, Type> savedVars = new HashMap<>(currentVars);
			Map<String, Type> blockVars = new HashMap<>(currentVars);

			WhileLoop wl = (WhileLoop) e;
			for (Expression ex : wl.expressions) {
				visitExpression(ex, blockVars);
			}

			currentVars.clear();
			currentVars.putAll(savedVars);
		}

	}

	private static String generateHtmlReport(File file, String filePath, ExpressionProcessor ep,
			List<String> semanticErrors) throws IOException {
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
			writer.println(generateLeftColumn(filePath, inputFileContents));
			writer.println(generateRightColumn(ep.vars, semanticErrors));
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
						        .container { display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
						        .left, .right { padding: 20px; background-color: #ffffff; border: 1px solid #82c0cc; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
						        pre { background-color: #e9ecef; padding: 15px; border-radius: 8px; overflow-x: auto; font-size: 14px; }
						        .error { background-color: #f8d7da; border-left: 4px solid #d9534f; padding: 10px; margin-bottom: 10px; border-radius: 4px; color: #721c24; }
						        .variable { background-color: #489fb5; border-left: 4px solid #16697a; padding: 10px; margin-bottom: 10px; border-radius: 4px; color: #ffffff; }
						        h2 { color: #489fb5; margin-top: 0; }
						        footer { text-align: center; margin-top: 40px; font-size: 12px; color: #888; }
						    </style>
						""",
				title);
	}

	private static String generateHtmlHeader(String title) {
		return "<h1>" + escapeHTML(title) + "</h1>";
	}

	private static String generateLeftColumn(String filePath, String contents) {
		return String.format("""
				    <div class="left">
				        <h2>File Path</h2>
				        <pre><code>%s</code></pre>
				        <h2>File Contents</h2>
				        <pre><code>%s</code></pre>
				    </div>
				""", escapeHTML(filePath), escapeHTML(contents));
	}

	private static String generateRightColumn(Map<String, Value> values, List<String> errors) {
		StringBuilder sb = new StringBuilder();
		sb.append("<div class=\"right\">");
		sb.append("<h2>Variables</h2>");
		if (values.isEmpty()) {
			sb.append("<p>No variables found.</p>");
		} else {
			for (Map.Entry<String, Value> entry : values.entrySet()) {
				sb.append("<div class=\"variable\">").append(escapeHTML(entry.getKey())).append(" : ")
						.append(escapeHTML(entry.getValue().toString())).append("</div>");
			}
		}

		sb.append("<h2>Type Errors</h2>");
		if (errors.isEmpty()) {
			sb.append("<p>No type errors found.</p>");
		} else {
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
						if (!line.contains("<footer>")) {
							writer.println(line);
						}
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
