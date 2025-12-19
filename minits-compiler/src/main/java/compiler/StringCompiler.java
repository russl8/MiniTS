package compiler;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.Expression.ClassDeclaration;
import model.MyErrorListener;
import org.antlr.v4.runtime.*;
import java.util.List;

public class StringCompiler extends Compiler {

    public StringCompiler() {
        super();
    }

    public List<ClassDeclaration> compileString(String source) {
        ExprParser parser = getParserFromString(source);
        if (parser == null || MyErrorListener.hasError) {
            System.err.println("Skipping compilation due to syntax errors or parser failure.");
            return null;
        }

        return super.compile(parser);
    }

    private static ExprParser getParserFromString(String source) {
        try {
            CharStream input = CharStreams.fromString(source);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new MyErrorListener());

            System.out.println(parser);
            return parser;
        } catch (Exception e) {
            System.err.println("Error parsing input string: " + e.getMessage());
            return null;
        }
    }
}
