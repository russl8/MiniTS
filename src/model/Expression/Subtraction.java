package model.Expression;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Subtraction extends BinaryExpression implements ArithmeticExpression {
	@Override
	public ReturnType getReturnType() {
		// TODO Auto-generated method stub
		return ReturnType.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Subtraction(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "-";
	}
}
