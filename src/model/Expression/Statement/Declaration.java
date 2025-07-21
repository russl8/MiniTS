package model.Expression.Statement;

import java.util.HashSet;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class Declaration extends Expression {

	public String var;
	public ReturnType type;
	public Expression expr;
	public boolean isInitialized;

	public ReturnType getReturnType() {
		return type;
	}

	public ExprType getExprType() {
		return expr.getExprType();
	}

	@Override
	public Set<String> getVariables() {
		Set<String> vars = new HashSet<>();
		vars.add(var);
		vars.addAll(expr.getVariables());
		return vars;
	}

	public Declaration(String var, ReturnType type, int line, int col) {
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

	public Declaration(String var, ReturnType type, Expression expr, int line, int col) {
		this(var, type, line, col);
		this.expr = expr;
		this.isInitialized = true;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (isInitialized) {
			return var + " : " + type + " = " + expr;
		} else {
			return var + " : " + type;
		}
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitDeclarationWithOptionalAssignment(this);
	}

}
