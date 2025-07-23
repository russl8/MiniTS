package model.Expression;

import java.util.Set;

import model.Expression.OperationVisitor.OperationVisitor;
import model.Expression.Statement.Statement;

public abstract class Expression {

	public abstract Set<String> getVariables();

	public abstract <T> T accept(OperationVisitor T);

	protected int line, col;

	public abstract int getLine();

	public abstract int getCol();

	public enum ReturnType {
		INT, BOOL, NONE, CHAR
	};

	public enum ExprType {
		LOGICAL, RELATIONAL, EQUALITY, ARITHMETIC, NONE
	};

	public abstract ReturnType getReturnType();

	public abstract ExprType getExprType();

}
