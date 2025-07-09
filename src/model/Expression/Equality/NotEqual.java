package model.Expression.Equality;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class NotEqual extends BinaryExpression  {

	@Override
	public ReturnType getReturnType() {
		// TODO Auto-generated method stub
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.EQUALITY;
	}

	public NotEqual(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "!=";
	}
}
