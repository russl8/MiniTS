package model.Expression.Statement;

import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Assignment extends Expression {
	public ReturnType getReturnType() {
		return ReturnType.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	public Expression expr;
	public String var;

	@Override
	public Set<String> getVariables() {
		return expr.getVariables();
	}

	public Assignment(String var, Expression expr) {
		this.var = var;
		this.expr = expr;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return var + " = " + expr;
	}
}
