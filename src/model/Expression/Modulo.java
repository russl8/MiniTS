package model.Expression;

public class Modulo extends BinaryExpression {
	public Modulo(Expression left, Expression right) {
		super.init(left, right);
		operation="%";
	}
}
