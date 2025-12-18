package model.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Expression.Visitor.OperationVisitor;

public class FunctionInvocation extends Expression {

	public String functionName;
	public List<Expression> arguments;
	public Type returnType;

	public FunctionInvocation(String functionName, int line, int col) {
		this.functionName = functionName;
		this.arguments = new ArrayList<>();
		this.line = line;
		this.col = col;
	}

	@Override
	public Set<String> getVariables() {
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitFunctionInvocation(this);
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
		return returnType;
	}

	public void setReturnType(Type type) {
		returnType = type;
	}

	@Override
	public ExprType getExprType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		String res = functionName + "(";
		for (Expression arg : arguments) {
			res += arg + ",";
		}
		res += ")\n";
		return res;
	}
}
