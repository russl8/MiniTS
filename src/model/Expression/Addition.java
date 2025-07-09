package model.Expression;

public class Addition extends BinaryExpression implements ArithmeticExpression {

	@Override
	public ReturnType getReturnType() {
		// TODO Auto-generated method stub
		return ReturnType.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Addition(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "+";
	}
}
