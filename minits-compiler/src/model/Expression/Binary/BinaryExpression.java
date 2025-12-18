package model.Expression.Binary;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression;

public abstract class BinaryExpression extends Expression {
	public Expression left, right;
	public String operation;

	public Set<String> getVariables() {
		HashSet<String> vars = new HashSet<>();
		vars.addAll(left.getVariables());
		vars.addAll(right.getVariables());
		return vars;
	}

	protected void init(Expression left, Expression right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public int getLine() {
		// TODO Auto-generated method stub
		return left.getLine();
	}

	public int getCol() {
		return left.getCol();
	}

	@Override
	public String toString() {
		return left.toString() + " " + operation + " " + right.toString();
	}

}
