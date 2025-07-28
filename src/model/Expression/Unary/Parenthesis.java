package model.Expression.Unary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.Visitor.OperationVisitor;

public class Parenthesis extends UnaryExpression {
	public Type getReturnType() {
		return expr.getReturnType();
	}

	public ExprType getExprType() {
		return expr.getExprType();
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
