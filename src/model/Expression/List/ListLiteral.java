package model.Expression.List;

import java.util.Set;

import model.Expression.Expression;
import model.Expression.OperationVisitor.OperationVisitor;

public class ListLiteral extends Expression {
	
	public ExprType exprType;
	
	
	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getLine() {
		return 0;
	}

	@Override
	public int getCol() {
		return 0;
	}

	@Override
	public PrimitiveType getReturnType() {
		return PrimitiveType.NONE;
	}

	@Override
	public ExprType getExprType() {
		return ExprType.NONE;
	}

}
