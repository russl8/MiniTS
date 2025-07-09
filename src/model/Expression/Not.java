package model.Expression;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Not extends UnaryExpression implements LogicalExpression {

	@Override
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.LOGICAL;
	}

	public Not(Expression expr) {
		super.init(expr);
		operation = "!";
	}

	public String toString() {
		return "!" + "(" + expr + ")";
	}
}
