package model.Expression;

import java.util.HashSet;
import java.util.Set;

import model.Expression.OperationVisitor.OperationVisitor;

public class Variable extends Expression {
	public String var;
	public PrimitiveType returnType;

	@Override
	public PrimitiveType getReturnType() {
		return returnType;
	}

	public ExprType getExprType() {
		if (this.returnType == PrimitiveType.INT) {
			return ExprType.ARITHMETIC;
		} else if (this.returnType == PrimitiveType.BOOL) {
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

	public Variable(String var, PrimitiveType returnType, int line, int col) {
		this.var = var;
		this.returnType = returnType;
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
		return var;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitVariable(this);
	}

}
