package compiler;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.Expression.ClassDeclaration;
import model.MyErrorListener;
import org.antlr.v4.runtime.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TextFileCompiler extends Compiler {

    public TextFileCompiler() {
        super();
    }

    public List<ClassDeclaration> compileFile(File file) {
        String filePath = file.getAbsolutePath();
        System.out.println("Processing file: " + filePath);

        ParseBundle bundle = getParserFromTextFile(filePath);

        if (bundle.parser == null) {
            System.err.println("Skipping file due to parser failure.");
            return null;
        }

        if (bundle.errorListener.hasError()) {
            System.err.println("Skipping file due to syntax errors.");
            for (String err : bundle.errorListener.getErrors()) {
                System.err.println(err);
            }
            return null;
        }

        return super.compile(bundle.parser);
    }

    private static ParseBundle getParserFromTextFile(String fileName) {
        try {
            CharStream input = CharStreams.fromFileName(fileName);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);

            MyErrorListener listener = new MyErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(listener);

            return new ParseBundle(parser, listener);
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName + " - " + e.getMessage());
            return new ParseBundle(null, new MyErrorListener());
        }
    }

    private record ParseBundle(ExprParser parser, MyErrorListener errorListener) {}
}
