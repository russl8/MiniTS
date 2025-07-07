package model.Expression;

public class Equal extends BinaryExpression {
	public Equal(Expression left, Expression right) {
		super.init(left, right);
		this.operation="==";
	}
}
