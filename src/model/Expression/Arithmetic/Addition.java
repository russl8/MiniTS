package model.Expression.Arithmetic;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Addition extends BinaryExpression  {

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
