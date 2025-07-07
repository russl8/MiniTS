package model.Expression;

public class Parenthesis extends UnaryExpression {
	public Parenthesis(Expression expr) {
		super.init(expr);
		operation = "";
	}

	public String toString() {
		return "(" + expr + ")";
	}
}
