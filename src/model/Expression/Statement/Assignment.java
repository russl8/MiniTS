package model.Expression.Statement;

import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.PrimitiveType;
import model.Expression.OperationVisitor.OperationVisitor;

public class Assignment extends Expression {
	public Expression expr;
	public String var;

	public PrimitiveType getReturnType() {
		return PrimitiveType.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	@Override
	public Set<String> getVariables() {
		return expr.getVariables();
	}

	public Assignment(String var, Expression expr, int line, int col) {
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
		return T.visitAssignment(this);
	}
}
