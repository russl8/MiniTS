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
        ParseBundle bundle = getParserFromString(source);

        if (bundle.parser == null) {
            System.err.println("Skipping compilation due to parser failure.");
            return null;
        }

        if (bundle.errorListener.hasError()) {
//            for (String err : bundle.errorListener.getErrors()) {
//                System.err.println(err);
//            }
            System.out.println("SYNTAX ERROR!");
            return null;
        }

        return super.compile(bundle.parser);
    }

    private static ParseBundle getParserFromString(String source) {
        try {
            CharStream input = CharStreams.fromString(source);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);

            MyErrorListener listener = new MyErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(listener);

            return new ParseBundle(parser, listener);
        } catch (Exception e) {
            System.err.println("Error parsing input string: " + e.getMessage());
            return new ParseBundle(null, new MyErrorListener());
        }
    }

    private record ParseBundle(ExprParser parser, MyErrorListener errorListener) {}
}
