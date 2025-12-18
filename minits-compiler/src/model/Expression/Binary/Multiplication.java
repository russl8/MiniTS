package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Visitor.OperationVisitor;

public class Multiplication extends BinaryExpression {

	@Override
	public Type getReturnType() {
		// TODO Auto-generated method stub
		return Type.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Multiplication(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "*";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitBinaryExpression(this);
	}

}
