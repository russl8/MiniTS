package model.Expression;

public class Or extends BinaryExpression {	
	public Or(Expression left, Expression right) {
		super.init(left, right);
		this.operation="||";
	}
}
