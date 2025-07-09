package model.Expression.Arithmetic;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Subtraction extends BinaryExpression  {
	@Override
	public ReturnType getReturnType() {
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
