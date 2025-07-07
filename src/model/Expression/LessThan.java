package model.Expression;

public class LessThan extends BinaryExpression {
	public LessThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation="<";
	}
}
