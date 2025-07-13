package model.Expression;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class BooleanLiteral extends Expression {
	public boolean val;

	public ReturnType getReturnType() {
		return ReturnType.BOOL;
	}

	public ExprType getExprType() {
		return ExprType.LOGICAL;
	}

	@Override
	public Set<String> getVariables() {
		Set<String> res = new HashSet<>();
		res.add("" + val);
		return res;
	}

	public BooleanLiteral(boolean val, int line, int col) {
		this.line = line;
		this.col = col;
		this.val = val;
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
	public String toString() {
		// TODO Auto-generated method stub
		return "" + val;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitBooleanLiteral(this);
	}
}
