package model.Expression.Unary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.Visitor.OperationVisitor;

public class Not extends UnaryExpression {

	@Override
	public Type getReturnType() {
		return Type.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.LOGICAL;
	}

	public Not(Expression expr) {
		super.init(expr);
		operation = "!";
	}

	@Override
	public int getCol() {
		// subtract 1 due to adding a character '!' to the left of the expression
		return expr.getCol() - 1;
	}

	public String toString() {
		return "!" + expr;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitNot(this);
	}
}
