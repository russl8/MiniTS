package dto;

import model.Value;

import java.util.Map;

public class ClassResult {
    public String className;
    public Map<String, Value> evaluatedVars;

    public ClassResult(String className, Map<String, Value> evaluatedVars) {
        this.className = className;
        this.evaluatedVars = evaluatedVars;
    }
}