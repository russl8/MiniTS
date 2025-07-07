package model.Expression;

public class Addition extends BinaryExpression {
	public Addition(Expression left, Expression right) {
		super.init(left, right);
		this.operation="+";
	}
}
