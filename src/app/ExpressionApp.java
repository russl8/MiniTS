package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.*;
import model.Expression.Expression;
import model.Expression.ExpressionProcessor;
import model.Expression.Statement.ClassDeclaration;
import model.Program.AntlrToProgram;
import model.Program.Program;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.w3c.dom.CDATASection;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * Note.
 * 	This class in the initial starter project is not expected to compile.
 * 	But it should compile after the skeleton code is generated into the `antlr` package.
 */

public class ExpressionApp {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.print("file name");
		} else {
			String fileName = args[0];
			ExprParser parser = getParser(fileName);
			ParseTree AST = parser.prog();

			if (MyErrorListener.hasError) {

			} else {
				AntlrToProgram progVisitor = new AntlrToProgram();
				Program prog = progVisitor.visit(AST);
				if (progVisitor.semanticErrors.isEmpty()) {
					processClasses(prog.expressions);

				} else {
					for (String err : progVisitor.semanticErrors) {
						System.out.println(err);
					}
				}
			}
		}
	}

	private static void processClasses(List<Expression> expressions) {
		System.out.println("========================================");

		for (Expression c : expressions) {
			ClassDeclaration cd = (ClassDeclaration) c;
			ExpressionProcessor ep = new ExpressionProcessor();
			for (Expression e : cd.expressions) {
				ep.processExpression(e);
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