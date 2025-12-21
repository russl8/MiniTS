package exception;

import java.util.List;

public class SyntaxException extends RuntimeException {
    private final List<String> errors;

    public SyntaxException(List<String> errors) {
        super("Syntax error");
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}