package model.Expression.Unary;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression;

public abstract class UnaryExpression extends Expression {
	public Expression expr;
	public String operation;

	public Set<String> getVariables() {
		HashSet<String> vars = new HashSet<>();
		vars.addAll(expr.getVariables());
		return vars;
	}

	protected void init(Expression expr) {
		this.expr = expr;
	}

	@Override
	public int getLine() {
		// TODO Auto-generated method stub
		return expr.getLine();
	}

	public abstract String toString();

}
