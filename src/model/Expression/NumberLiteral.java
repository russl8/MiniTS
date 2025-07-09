package model.Expression;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

public class NumberLiteral extends Expression implements ArithmeticExpression {
	public int val;

	public ReturnType getReturnType() {
		return ReturnType.INT;
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

	public NumberLiteral(int val) {
		this.val = val;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "" + val;
	}
}
