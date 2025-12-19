package model.Expression.BlockContainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Visitor.OperationVisitor;

public class ForLoop extends Expression implements BlockContainer {
	public List<Expression> expressions;
	public Expression initialization;
	public Expression condition;
	public Expression update;

	public ForLoop(Expression initialization, Expression condition, Expression update, int line, int col) {
		this.initialization = initialization;
		this.condition = condition;
		this.update = update;
		this.line = line;
		this.col = col;
		this.expressions = new ArrayList<>();
	}

	@Override
	public void addExpression(Expression e) {
		this.expressions.add(e);
	}

	@Override
	public List<Expression> getExpressions() {
		return this.expressions;
	}

	@Override
	public Set<String> getVariables() {
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitForLoop(this);
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
	public Type getReturnType() {
		return Type.NONE;
	}

	@Override
	public ExprType getExprType() {
		// TODO Auto-generated method stub
		return ExprType.NONE;
	}

	@Override
	public String toString() {
		String res = "for (\n";
		res += initialization + "; " + condition + "; " + update + ") {\n";
		for (Expression e : this.expressions) {
			res += e + "\n";
		}
		res += "}\n";
		return res;
	}

}
