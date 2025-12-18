package dto;

import model.Expression.ClassDeclaration;

import java.util.List;

public class CompileResult {
    public final boolean success;
    public final List<String> semanticErrors;
    public final List<ClassDeclaration> classes;

    public CompileResult(
            boolean success,
            List<String> semanticErrors,
            List<ClassDeclaration> classes
    ) {
        this.success = success;
        this.semanticErrors = semanticErrors;
        this.classes = classes;
    }
}
