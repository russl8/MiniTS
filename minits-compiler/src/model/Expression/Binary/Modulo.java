package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Visitor.OperationVisitor;

public class Modulo extends BinaryExpression {
	@Override
	public Type getReturnType() {
		// TODO Auto-generated method stub
		return Type.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Modulo(Expression left, Expression right) {
		super.init(left, right);
		operation = "%";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitBinaryExpression(this);
	}
}
