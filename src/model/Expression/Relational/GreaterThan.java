package model.Expression.Relational;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class GreaterThan extends BinaryExpression  {
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.RELATIONAL;
	}

	public GreaterThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation = ">";
	}
}
