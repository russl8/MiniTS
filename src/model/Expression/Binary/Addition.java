package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.OperationVisitor.OperationVisitor;

public class Addition extends BinaryExpression {

	@Override
	public Type getReturnType() {
		// TODO Auto-generated method stub
		return Type.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Addition(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "+";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitAddition(this);
	}
}
