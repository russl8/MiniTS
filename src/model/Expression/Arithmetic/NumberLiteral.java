package model.Expression.Arithmetic;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class NumberLiteral extends Expression {
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

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitNumberLiteral(this);

	}

}
