package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class Subtraction extends BinaryExpression {
	@Override
	public ReturnType getReturnType() {
		return ReturnType.INT;
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
