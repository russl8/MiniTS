package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpressionEvaluator {
	List<Expression> list;
	public Map<String, String> values;
	
	public ExpressionEvaluator(List<Expression> list) {
		this.list = list;
		values = new HashMap<>();
	}
	
	public List<String> getEvaluationResults() {
		List<String> evaluations = new ArrayList<>();
		
		for (Expression e: list) {
			if (e instanceof Variable) {
				Variable decl = (Variable) e;
				values.put(decl.name, decl.value.toString());
			}
			else {
				String input = e.toString();
				String result = getEvalResult(e);
				evaluations.add(input + " is " + result);
			}
		}
		
		return evaluations;
	}
	
	private String getEvalResult(Expression expr) {
        if (expr instanceof Number) {
            return ((Number) expr).toString();
        } else if (expr instanceof Variable) {
            return ((Variable) expr).toString();
        } else if (expr instanceof BinaryOperation binOp) {
            return binOp.evaluate().toString();
        }
        return "";
    }
}
