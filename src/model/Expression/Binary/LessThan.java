package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.PrimitiveType;
import model.Expression.OperationVisitor.OperationVisitor;

public class LessThan extends BinaryExpression  {
	public PrimitiveType getReturnType() {
		return PrimitiveType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.RELATIONAL;

	}

	public LessThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "<";
	}
	
	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitLessThan(this);
	}
}
