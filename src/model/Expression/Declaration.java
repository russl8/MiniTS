package model.Expression;

import java.util.HashSet;
import java.util.Set;

public class Declaration extends Statement {

	public String var;
	public String type;
	public Expression expr;

	@Override
	public Set<String> getVariables() {
		Set<String> vars = new HashSet<>();
		vars.add(var);
		vars.addAll(expr.getVariables());
		return vars;
	}

	public Declaration(String var, String type) {
		this.var = var;
		this.type = type;
		// type not int or bool error
	}

	public Declaration(String var, String type, Expression expr) {
		this(var, type);
		this.expr = expr;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return var + " : " + type + " = " + expr;
	}

}
