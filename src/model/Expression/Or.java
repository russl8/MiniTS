package model.Expression;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Or extends BinaryExpression implements LogicalExpression {

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.LOGICAL;
	}

	public Or(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "||";
	}
}
