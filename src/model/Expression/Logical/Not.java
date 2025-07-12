package model.Expression.Logical;

import model.Expression.Expression;
import model.Expression.UnaryExpression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class Not extends UnaryExpression {

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

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitNot(this);
	}
}
