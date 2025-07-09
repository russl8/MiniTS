package model.Expression.Logical;

import model.Expression.Expression;
import model.Expression.UnaryExpression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Not extends UnaryExpression  {

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
