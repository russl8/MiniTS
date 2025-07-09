package model.Expression;

import java.util.HashSet;
import java.util.Set;

public class Variable extends Expression {
	public String var;
	public ReturnType returnType;

	@Override
	public ReturnType getReturnType() {
		return returnType;
	}

	public ExprType getExprType() {
		if (this.returnType == ReturnType.INT) {
			return ExprType.ARITHMETIC;
		} else if (this.returnType == ReturnType.BOOL) {
			return ExprType.LOGICAL;
		}
		return ExprType.NONE;
	}

	@Override
	public Set<String> getVariables() {
		HashSet<String> res = new HashSet<>();
		res.add(var);
		return res;
	}

	public Variable(String var, ReturnType returnType) {
		this.var = var;
		this.returnType = returnType;
	}

	@Override
	public String toString() {
		return var;
	}

}
