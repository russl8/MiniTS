package model.Expression;

import java.util.Set;

public class Assignment extends Statement {
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
}
