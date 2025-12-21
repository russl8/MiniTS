package compiler;

import antlr.ExprLexer;
import antlr.ExprParser;
import exception.SyntaxException;
import model.Expression.ClassDeclaration;
import model.MyErrorListener;
import org.antlr.v4.runtime.*;

import java.util.List;

/**
 * Compiles MiniTS source code provided as a String.
 *
 * Pipeline:
 * 1) Lex + parse
 * 2) If any syntax errors were reported by MyErrorListener -> throw SyntaxException
 * 3) Otherwise run the normal compilation pipeline (AST build + semantic checks + eval) via Compiler.compile(...)
 *
 * NOTE: This class assumes you are using the *new* instance-based MyErrorListener:
 *  - listener.hasError()
 *  - listener.getErrors()
 */
public class StringCompiler extends Compiler {

    public StringCompiler() {
        super();
    }

    /**
     * Compile MiniTS source string into a list of ClassDeclaration results.
     *
     * @param source MiniTS program source
     * @return compiled classes
     * @throws SyntaxException if the program has grammar/syntax errors
     */
    public List<ClassDeclaration> compileString(String source) {
        if (source == null) {
            throw new IllegalArgumentException("source cannot be null");
        }

        // 1) Build parser + attach fresh listener for this request
        CharStream input = CharStreams.fromString(source);
        ExprLexer lexer = new ExprLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ExprParser parser = new ExprParser(tokens);

        MyErrorListener listener = new MyErrorListener();
        parser.removeErrorListeners();
        parser.addErrorListener(listener);

        // 2) "verifyGrammar": build the parse tree once
        ExprParser.ProgContext tree = parser.prog();

        // 3) If syntax errors occurred, stop BEFORE AST/visitors
        if (listener.hasError()) {
            throw new SyntaxException(listener.getErrors());
        }

        return super.compile(tree);
    }
}
