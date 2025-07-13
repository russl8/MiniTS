package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.*;
import model.Expression.Expression;
import model.Expression.ExpressionProcessor;
import model.Expression.OperationVisitor.ExpressionTypeChecker;
import model.Expression.Statement.ClassDeclaration;
import model.Program.AntlrToProgram;
import model.Program.Program;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.CDATASection;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Note.
 * 	This class in the initial starter project is not expected to compile.
 * 	But it should compile after the skeleton code is generated into the `antlr` package.
 */

public class ExpressionApp {

	private static String filePath;

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Error: missing filename argument");
		} else {
			filePath = args[0];
			ExprParser parser = getParser(filePath);
			ParseTree AST = parser.prog();

			if (MyErrorListener.hasError) {
			} else {
				try {
					String fileName = new File(filePath).getName();
					fileName = fileName.substring(0, fileName.lastIndexOf('.'));
					PrintWriter writer = new PrintWriter(
							new FileWriter("src/tests/output/" + fileName + "output.html"));
					writer.println("<html><body>");
					List<String> semanticErrors = new ArrayList<>();

					// Visitors
					AntlrToProgram progVisitor = new AntlrToProgram(semanticErrors);
					Program prog = progVisitor.visit(AST);

					// Perform type checking on class expressions
					for (Expression classExpr : prog.expressions) {
						ClassDeclaration cd = (ClassDeclaration) classExpr;
						for (Expression e : cd.expressions) {
							ExpressionTypeChecker typeCheckerVisitor = new ExpressionTypeChecker(semanticErrors);
							e.accept(typeCheckerVisitor);
						}
					}

					// Evaluation
					if (semanticErrors.isEmpty()) {
						for (Expression classExpr : prog.expressions) {
							ClassDeclaration cd = (ClassDeclaration) classExpr;
							writer.println("<p>class " + cd.className
									+ (cd.superClass != null ? " extends " + cd.superClass : "") + "</p>");
							ExpressionProcessor ep = new ExpressionProcessor();
							for (Expression e : cd.expressions) {
								ep.evaluateExpression(e);
							}

							/**
							 * Replac all printing operations with e.accept(writerVisitor)
							 */
							for (String var : ep.values.keySet()) {
								writer.println("<p>&#9;" + var + " : " + ep.values.get(var) + "</p>");
							}
						}
					} else {

						for (String err : progVisitor.semanticErrors) {
							writer.println("<p>" + err + "</p>");
						}
					}

					writer.println("</body></html>");
					writer.close();
					System.out.println("Generated file output in " + "src/tests/output/" + fileName + "output.html");

				} catch (Exception e) {
					System.out.println("Error: " + e);
				} // end of try/catch

			}

		}
	}

	private static ExprParser getParser(String fileName) {
		ExprParser parser = null;

		try {
			CharStream input = CharStreams.fromFileName(fileName);
			ExprLexer lexer = new ExprLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			parser = new ExprParser(tokens);

			parser.removeErrorListeners();
			parser.addErrorListener(new MyErrorListener());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parser;
	}

}