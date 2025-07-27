package model.Expression.Declaration;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.Visitor.OperationVisitor;

public class PrimitaveDeclaration extends Declaration {

	public String var;
	public Type type;

	public Type getReturnType() {
		return type;
	}

	public ExprType getExprType() {
		return initialization.getExprType();
	}

	@Override
	public Set<String> getVariables() {
		Set<String> vars = new HashSet<>();
		vars.add(var);
		vars.addAll(initialization.getVariables());
		return vars;
	}

	public PrimitaveDeclaration(String var, Type type, int line, int col) {
		this.var = var;
		this.type = type;
		this.isInitialized = false;
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

	public PrimitaveDeclaration(String var, Type type, Expression initialization, int line, int col) {
		this(var, type, line, col);
		this.initialization = initialization;
		this.isInitialized = true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (isInitialized) {
			return var + " : " + type + " = " + initialization;
		} else {
			return var + " : " + type;
		}
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitPrimitaveDeclaration(this);
	}

}
