package model.Expression.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Visitor.OperationVisitor;

public class WhileLoop extends Expression {

	public Expression cond;
	public List<Expression> expressions; // loop body

	public WhileLoop(Expression cond, int line, int col) {
		this.line = line;
		this.col = col;
		this.cond = cond;
		this.expressions = new ArrayList<>();
	}

	public void addExpression(Expression e) {
		expressions.add(e);

	}

	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitWhileLoop(this);
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
		return ExprType.NONE;
	}

	@Override
	public String toString() {
		String res = "while (" + cond + ")" + " {\n";
		for (Expression e : expressions) {
			res += e + "\n";
		}
		res += "}";
		return res;
	}

}
