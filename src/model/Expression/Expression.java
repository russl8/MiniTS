package model.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import model.Expression.Visitor.OperationVisitor;

public abstract class Expression {

	public abstract Set<String> getVariables();

	public abstract <T> T accept(OperationVisitor T);

	protected int line, col;

	public abstract int getLine();

	public abstract int getCol();

	public enum Type {
	    INT, BOOL, NONE, CHAR, LIST_INT, LIST_BOOL, LIST_CHAR;

	    private static final Map<String, Type> MAP = new HashMap<>();

	    static {
	        for (Type type : values()) {
	            MAP.put(type.name(), type);
	        }
	        MAP.put("LIST[INT]", LIST_INT);
	        MAP.put("LIST[BOOL]", LIST_BOOL);
	        MAP.put("LIST[CHAR]", LIST_CHAR);

	    }

	    public static Type fromString(String s) {
	        return MAP.get(s.toUpperCase()); 
	    }
	}
	public enum ExprType {
		LOGICAL, RELATIONAL, EQUALITY, ARITHMETIC, NONE
	};

	public abstract Type getReturnType();

	public abstract ExprType getExprType();

}
