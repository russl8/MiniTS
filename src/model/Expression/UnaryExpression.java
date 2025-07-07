package model.Expression;

import java.util.HashSet;
import java.util.Set;

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
	
	public abstract String toString();

}
