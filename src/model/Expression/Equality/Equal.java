package model.Expression.Equality;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Equal extends BinaryExpression  {
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.EQUALITY;
	}

	public Equal(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "==";
	}
}
