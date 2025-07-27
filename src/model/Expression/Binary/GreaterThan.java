package model.Expression.Binary;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.Visitor.OperationVisitor;

public class GreaterThan extends BinaryExpression {
	public Type getReturnType() {
		return Type.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.RELATIONAL;
	}

	public GreaterThan(Expression left, Expression right) {
		super.init(left, right);
		this.operation = ">";
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitBinaryExpression(this);
	}
}
