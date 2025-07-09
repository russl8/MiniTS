package model.Expression.Relational;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class LessThan extends BinaryExpression  {
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.RELATIONAL;

	}

	public LessThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "<";
	}
}
