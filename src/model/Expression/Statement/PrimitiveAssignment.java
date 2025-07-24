package model.Expression.Statement;

import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.OperationVisitor.OperationVisitor;

public class PrimitiveAssignment extends Expression {
	public Expression expr;
	public String var;

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
