package model.Assignment;

import java.util.Set;

import model.Expression.Expression;
import model.Expression.Visitor.OperationVisitor;

public class ListAssignment extends Assignment {

	public ListAssignment(String var, Expression expr, int line, int col) {
		this.var = var;
		this.expr = expr; // Typecheck: should be a list
		this.line = line;
		this.col = col;
	}

	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitListAssignment(this);
	}

	@Override
	public int getLine() {
		return line;
	}

	@Override
	public int getCol() {
		return col;
	}

	@Override
	public Type getReturnType() {
		return null;
	}

	@Override
	public ExprType getExprType() {
		return null;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return var + " = " + expr;
	}

}
