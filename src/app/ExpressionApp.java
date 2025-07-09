package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.*;
import model.Expression.Expression;
import model.Program.AntlrToProgram;
import model.Program.Program;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.HashSet;
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
                    // Your implementation goes here.

                } else {
                    for (String err : progVisitor.semanticErrors) {
                        System.out.println(err);
                    }
                }
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