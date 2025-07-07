package model.Expression;

public class GreaterThan extends BinaryExpression {
	public GreaterThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation=">";
	}
}
