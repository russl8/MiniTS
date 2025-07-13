package model.Expression.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class IfStatement extends Expression {
	public Expression cond;
	public List<Expression> expressions;

	public ReturnType getReturnType() {
		return ReturnType.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	public IfStatement(Expression cond, int line, int col) {
		this.cond = cond;
		this.expressions = new ArrayList<>();
		this.line = line;
		this.col = col;
	}

	@Override
	public int getLine() {
		// TODO Auto-generated method stub
		return line;
	}

	@Override
	public int getCol() {
		// TODO Auto-generated method stub
		return col;
	}

	public Set<String> getVariables() {
		Set<String> res = new HashSet<>();

		res.addAll(cond.getVariables());
		for (Expression e : expressions) {
			res.addAll(e.getVariables());
		}
		return res;
	}

	public void addExpression(Expression e) {
		this.expressions.add(e);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		String res = "if (" + cond + ") {\n";
		for (Expression e : expressions) {
			res += "        " + e + "\n";
		}
		res += "    }";
		return res;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitIfStatement(this);
	}
}
