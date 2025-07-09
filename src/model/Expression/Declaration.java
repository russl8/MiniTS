package model.Expression;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class Declaration extends Expression {

	public String var;
	public ReturnType type;
	public Expression expr;
	public boolean isInitialized;

	public ReturnType getReturnType() {
		return ReturnType.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	@Override
	public Set<String> getVariables() {
		Set<String> vars = new HashSet<>();
		vars.add(var);
		vars.addAll(expr.getVariables());
		return vars;
	}

	public Declaration(String var, ReturnType type) {
		this.var = var;
		this.type = type;
		this.isInitialized = false;
		// type not int or bool error
	}

	public Declaration(String var, ReturnType type, Expression expr) {
		this(var, type);
		this.expr = expr;
		this.isInitialized = true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (isInitialized) {
			return var + " : " + type + " = " + expr;
		} else {
			return var + " : " + type;
		}
	}

}
