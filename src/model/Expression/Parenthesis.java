package model.Expression;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Parenthesis extends UnaryExpression {
	public ReturnType getReturnType() {
		// TODO Auto-generated method stub
		return ReturnType.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	public Parenthesis(Expression expr) {
		super.init(expr);
		operation = "";
	}

	public String toString() {
		return "(" + expr + ")";
	}
}
