package model.Expression.Logical;

import model.Expression.BinaryExpression;
import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class And extends BinaryExpression  {
	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.LOGICAL;
	}

	public And(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "&&";
	}
}
