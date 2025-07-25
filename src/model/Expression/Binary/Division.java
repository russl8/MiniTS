package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.OperationVisitor.OperationVisitor;

public class Division extends BinaryExpression {

	@Override
	public Type getReturnType() {
		// TODO Auto-generated method stub
		return Type.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	public Division(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "==";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitBinaryExpression(this);
	}
}
