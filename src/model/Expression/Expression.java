package model.Expression;

import java.util.Set;

import model.Expression.Statement.Statement;
import model.Expression.Visitor.OperationVisitor;

public abstract class Expression {

	public abstract Set<String> getVariables();

	public abstract <T> T accept(OperationVisitor T);

	protected int line, col;

	public abstract int getLine();

	public abstract int getCol();

	public enum Type {
		INT, BOOL, NONE, CHAR, LIST_INT, LIST_BOOL, LIST_CHAR;
	}

	public enum ExprType {
		LOGICAL, RELATIONAL, EQUALITY, ARITHMETIC, NONE
	};

	public abstract Type getReturnType();

	public abstract ExprType getExprType();

}
