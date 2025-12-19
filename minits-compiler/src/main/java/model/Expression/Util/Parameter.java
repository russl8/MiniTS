package model.Expression.Util;

import model.Expression.Expression.Type;

public class Parameter {
	public String name;
	public Type type;
	public int line, col;

	public Parameter(String name, Type type, int line, int col) {
		this.name = name;
		this.type = type;
		this.col = col;
		this.line = line;
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	@Override
	public String toString() {
		return name + " : " + type;
	}
}
