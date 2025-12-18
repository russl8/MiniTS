package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Visitor.OperationVisitor;

public class Or extends BinaryExpression {

	@Override
	public Type getReturnType() {
		return Type.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.LOGICAL;
	}

	public Or(Expression left, Expression right) {
		super.init(left, right);
		this.operation = "||";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitBinaryExpression(this);
	}
}
