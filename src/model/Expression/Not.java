package model.Expression;

public class Not extends UnaryExpression {
	public Not(Expression expr) {
		super.init(expr);
		operation = "!";
	}

	public String toString() {
		return "!" + "(" + expr + ")";
	}
}
