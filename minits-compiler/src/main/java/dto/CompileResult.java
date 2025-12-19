package dto;

import java.util.List;

public class CompileResult{
    public boolean success;
    public List<String> semanticErrors;
    public List<ClassResult> classes;

    public CompileResult(boolean success, List<String> semanticErrors, List<ClassResult> classes) {
        this.success = success;
        this.semanticErrors = semanticErrors;
        this.classes = classes;
    }
}
