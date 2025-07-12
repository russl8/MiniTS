package model.Expression;

import java.util.Set;

import model.Expression.OperationVisitor.OperationVisitor;
import model.Expression.Statement.Statement;

public abstract class Expression  {
	public abstract Set<String> getVariables();
	public abstract <T> T accept(OperationVisitor T);
	public enum ReturnType {
		INT, BOOL, NONE
	};

	public enum ExprType {
		LOGICAL, RELATIONAL, EQUALITY, ARITHMETIC, NONE
	};

	public abstract ReturnType getReturnType();

	public abstract ExprType getExprType();

}
