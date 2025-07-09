package model.Expression;

import java.util.Set;

public abstract class Expression extends Statement {
	public abstract Set<String> getVariables();

	public enum ReturnType {
		INT, BOOL, NONE
	};

	public enum ExprType {
		LOGICAL, RELATIONAL, EQUALITY, ARITHMETIC, NONE
	};

	public abstract ReturnType getReturnType();

	public abstract ExprType getExprType();

}
