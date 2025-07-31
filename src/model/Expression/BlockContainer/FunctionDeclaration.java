package model.Expression.BlockContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Util.Parameter;
import model.Expression.Visitor.OperationVisitor;

public class FunctionDeclaration extends Expression {
	public List<Parameter> parameters;
	public List<Expression> expressions;

	public String functionName;
	public Type returnType;

	public FunctionDeclaration(String functionName, Type returnType, int line, int col) {
		this.functionName = functionName;
		this.returnType = returnType;
		this.parameters = new ArrayList<>();
		this.expressions = new ArrayList<>();
		this.line = line;
		this.col = col;
	}

	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitFunctionDeclaration(this);
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

	@Override
	public ExprType getExprType() {
		return ExprType.NONE;
	}

	public void addExpression(Expression e) {
		this.expressions.add(e);
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	public void addParameter(Parameter p) {
		this.parameters.add(p);
	}

	public List<Parameter> getParameters() {
		return parameters;
	}

	@Override
	public String toString() {
		String res = "function " + functionName + " ( ";
		for (Parameter p : parameters) {
			res += p + " ";
		}
		res += ") : " + this.returnType + " {\n";

		for (Expression e : expressions) {
			res += e + "\n";
		}
		res += "}\n";
		return res;
	}

}
