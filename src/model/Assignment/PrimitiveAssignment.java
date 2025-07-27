package model.Assignment;

import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.Visitor.OperationVisitor;

public class PrimitiveAssignment extends Assignment {


	public Type getReturnType() {
		return Type.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	@Override
	public Set<String> getVariables() {
		return expr.getVariables();
	}

	public PrimitiveAssignment(String var, Expression expr, int line, int col) {
		this.var = var;
		this.expr = expr;
		this.line = line;
		this.col = col;
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
	public String toString() {
		// TODO Auto-generated method stub
		return var + " = " + expr;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitPrimitiveAssignment(this);
	}
}
