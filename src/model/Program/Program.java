package model.Program;

import java.util.ArrayList;
import java.util.List;

import model.Expression.Expression;

public class Program {
	public List<Expression> expressions;

	public Program() {
		this.expressions = new ArrayList<>();
	}

	public void addExpression(Expression e) {
		if (e != null) {
			this.expressions.add(e);
		}
	}
}