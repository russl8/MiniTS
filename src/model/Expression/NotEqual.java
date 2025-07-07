package model.Expression;

public class NotEqual extends BinaryExpression {
	public NotEqual(Expression left, Expression right) {
		super.init(left, right);
		this.operation="!=";
	}
}
