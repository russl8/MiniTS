package model;

import java.util.ArrayList;
import java.util.List;

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