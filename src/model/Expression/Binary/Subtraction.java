package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.PrimitiveType;
import model.Expression.OperationVisitor.OperationVisitor;

public class Subtraction extends BinaryExpression {
	@Override
	public PrimitiveType getReturnType() {
		return PrimitiveType.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Subtraction(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "-";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitSubtraction(this);
	}
}
