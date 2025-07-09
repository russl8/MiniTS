package model.Expression.Relational;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class LessEqualThan extends BinaryExpression  {
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.RELATIONAL;
	}

	public LessEqualThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "<=";
	}
}
