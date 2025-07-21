package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class Modulo extends BinaryExpression {
	@Override
	public ReturnType getReturnType() {
		// TODO Auto-generated method stub
		return ReturnType.INT;
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
		return T.visitModulo(this);
	}
}
