package model.Expression;

public class Subtraction extends BinaryExpression {
	public Subtraction(Expression left, Expression right) {
		super.init(left, right);
		this.operation="-";
	}
}
