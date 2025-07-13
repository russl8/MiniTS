package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

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
	
	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitAnd(this);
	}
}
