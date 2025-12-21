package compiler;

import antlr.ExprLexer;
import antlr.ExprParser;
import exception.SyntaxException;
import model.Expression.ClassDeclaration;
import model.MyErrorListener;
import org.antlr.v4.runtime.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Compiles MiniTS source code from a text file.
 * <p>
 * Pipeline:
 * 1) Lex + parse
 * 2) If any syntax errors were reported -> throw SyntaxException
 * 3) Otherwise run the normal compilation pipeline via Compiler.compile(...)
 */
public class TextFileCompiler extends Compiler {

    public TextFileCompiler() {
        super();
    }

    /**
     * Compile a MiniTS source file.
     *
     * @param file input .txt file
     * @return compiled classes
     * @throws SyntaxException if the file has grammar/syntax errors
     */
    public List<ClassDeclaration> compileFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("file does not exist: " + file);
        }

        System.out.println("Processing file: " + file.getAbsolutePath());

        try {
            // 1) Build parser + fresh listener
            CharStream input = CharStreams.fromFileName(file.getAbsolutePath());
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);

            MyErrorListener listener = new MyErrorListener();
            parser.removeErrorListeners();
            parser.addErrorListener(listener);

            // 2) verifyGrammar: build parse tree once
            ExprParser.ProgContext tree = parser.prog();

            if (listener.hasError()) {
                throw new SyntaxException(listener.getErrors());
            }
            tokens.seek(0);

            // 3) Compile using existing pipeline
            // Option A (recommended): change Compiler.compile(...) to accept ProgContext
            return super.compile(tree);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read source file: " + file.getAbsolutePath(), e);
        }
    }
}
