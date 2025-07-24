package model.Expression;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.OperationVisitor.OperationVisitor;

public class NumberLiteral extends Expression {
	public int val;

	public Type getReturnType() {
		return Type.INT;
	}

	public ExprType getExprType() {
		return ExprType.ARITHMETIC;
	}

	@Override
	public Set<String> getVariables() {
		Set<String> res = new HashSet<>();
		res.add("" + val);
		return res;
	}

	public NumberLiteral(int val, int line, int col) {
		this.val = val;
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

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + val;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitNumberLiteral(this);

	}

}
