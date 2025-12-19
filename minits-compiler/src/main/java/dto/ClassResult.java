package dto;

import java.util.Map;

public class ClassResult {
    public String className;
    public Map<String, Object> evaluatedVars;

    public ClassResult(String className, Map<String, Object> evaluatedVars) {
        this.className = className;
        this.evaluatedVars = evaluatedVars;
    }
}