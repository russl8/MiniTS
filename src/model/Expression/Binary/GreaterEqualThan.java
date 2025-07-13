package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class GreaterEqualThan extends BinaryExpression {
	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.RELATIONAL;
	}

	public GreaterEqualThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation = ">=";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitGreaterEqualThan(this);
	}

}
