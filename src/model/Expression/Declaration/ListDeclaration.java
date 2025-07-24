package model.Expression.Declaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Value;
import model.Expression.AntlrToExpression;
import model.Expression.Expression;
import model.Expression.OperationVisitor.OperationVisitor;

public class ListDeclaration extends Expression implements Declaration {

	public Expression list;
	public Type type;
	public String var;
	private int line;
	private int col;
	public boolean isInitialized;
	
	public ListDeclaration(String var, Type type, int line, int col) {
		this.type = type;
		this.var = var;
		this.line = line;
		this.col = col;
		this.isInitialized = false;
	}

	public ListDeclaration(String var, Expression list, Type type, int line, int col) {
		this(var, type, line, col);
		this.list = list;
		this.isInitialized = true;
	}

	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitListDeclaration(this);
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
		return this.type;
	}

	@Override
	public ExprType getExprType() {
		return ExprType.NONE;
	}

	@Override
	public String toString() {
		String res = this.var + " : list [" + type + "] = " + this.list;
		return res;
	}
}
