package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.PrimitiveType;
import model.Expression.OperationVisitor.OperationVisitor;

public class NotEqual extends BinaryExpression {

	@Override
	public PrimitiveType getReturnType() {
		// TODO Auto-generated method stub
		return PrimitiveType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.EQUALITY;
	}

	public NotEqual(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "!=";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitNotEqual(this);
	}
	

}
