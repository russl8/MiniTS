package model.Expression;

import model.Expression.Expression.ExprType;

public class And extends BinaryExpression implements LogicalExpression {
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
