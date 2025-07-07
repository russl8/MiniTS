package model.Expression;

public class Multiplication extends BinaryExpression {
	
	public Multiplication() {
		super.init(left, right);
		this.operation = "*";
	}
}
