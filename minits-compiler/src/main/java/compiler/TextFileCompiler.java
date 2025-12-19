package compiler;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.Expression.ClassDeclaration;
import model.MyErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

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

        ExprParser parser = getParserFromTextFile(filePath);
        if (parser == null || MyErrorListener.hasError) {
            System.err.println("Skipping file due to syntax errors or parser failure.");
            return null;
        }
        return super.compile(parser);
    }

    private static ExprParser getParserFromTextFile(String fileName) {
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
