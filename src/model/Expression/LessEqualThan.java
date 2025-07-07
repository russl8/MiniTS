package model.Expression;

public class LessEqualThan extends BinaryExpression {
	public LessEqualThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation="<=";
	}
}
