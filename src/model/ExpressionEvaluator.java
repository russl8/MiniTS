package model;

import java.util.HashMap;
import java.util.Map;

public class ExpressionEvaluator {
    private Map<String, Integer> variables;

    public ExpressionEvaluator() {
        this.variables = new HashMap<>();
    }

    public void setVariable(String name, int value) {
        variables.put(name, value);
    }

    public int getVariable(String name) {
        return variables.getOrDefault(name, 0);
    }

    public int evaluate(Expression expr) {
        if (expr instanceof Number) {
            return ((Number) expr).value;
        } else if (expr instanceof Variable) {
            return getVariable(((Variable) expr).name);
        } else if (expr instanceof BinaryOperation binOp) {
            int leftValue = evaluate(binOp.left);
            int rightValue = evaluate(binOp.right);

            if ("+".equals(binOp.operator)) {
                return leftValue + rightValue;
            } else if ("*".equals(binOp.operator)) {
                return leftValue * rightValue;
            }
        }
        return 0;
    }

    public Map<String, Integer> getVariables() {
        return new HashMap<>(variables);
    }
}