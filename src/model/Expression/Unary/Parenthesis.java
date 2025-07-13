package model.Expression.Unary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

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

	
	@Override
	public int getCol() {
		// subtract 1 due to adding a character '(' to the left of the expression
		return expr.getCol() - 1;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitParenthesis(this);
	}
}
