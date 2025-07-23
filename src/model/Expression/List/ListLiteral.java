package model.Expression.List;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.OperationVisitor.OperationVisitor;

public class ListLiteral extends Expression {

	public ExprType exprType;

	/**
	 * List<Expression> Elements;
	 * 
	 * Constructor: - get line, col
	 * 
	 */
	public List<Expression> items;

	public ListLiteral(int line, int col) {
		this.items = new ArrayList<>();
		this.line = line;
		this.col = col;
	}

	public void add(Expression e) {
		items.add(e);
	}

	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return null;
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
	public PrimitiveType getReturnType() {
		return PrimitiveType.NONE;
	}

	@Override
	public ExprType getExprType() {
		return ExprType.NONE;
	}

	public String toString() {
		String res = "[ ";
		for (Expression e : items) {
			res += e.toString() + ", ";
		}
		res += "] ";
		return res;
	}

}
