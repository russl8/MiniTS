package model.Expression;

public class And extends BinaryExpression {
	public And(Expression left, Expression right) {
		super.init(left, right);
		this.operation="&&";
	}
}
