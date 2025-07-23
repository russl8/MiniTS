package model.Expression.Declaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import model.Expression.AntlrToExpression;
import model.Expression.Expression;
import model.Expression.Value;
import model.Expression.OperationVisitor.OperationVisitor;

public class ListDeclaration extends Expression implements Declaration {

	public Expression items;
	public PrimitiveType itemType;
	public String var;
	private int line;
	private int col;
	public boolean isInitialized;

	public ListDeclaration(String var, PrimitiveType itemType, int line, int col) {
		this.itemType = itemType;
		this.var = var;
		this.line = line;
		this.col = col;
		this.isInitialized = false;
	}

	public ListDeclaration(String var, Expression items, PrimitiveType itemType, int line, int col) {
		this(var, itemType, line, col);
		this.items = items;
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
	public PrimitiveType getReturnType() {
		return this.itemType;
	}

	@Override
	public ExprType getExprType() {
		return ExprType.NONE;
	}

	@Override
	public String toString() {
		String res = this.var + " : list [" + itemType + "] = " + this.items;
		return res;
	}
}
