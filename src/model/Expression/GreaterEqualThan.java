package model.Expression;

public class GreaterEqualThan extends BinaryExpression {
	public GreaterEqualThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation=">=";
	}
}
