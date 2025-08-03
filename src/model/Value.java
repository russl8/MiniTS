package model;

import model.Expression.Expression;
import model.Expression.Expression.Type;

public class Value {
	public Type type;
	private Object value;

	public Value(Type type) {
		this.type = type;
		this.value = null;
	}

	public Value(Type type, Object value) {
		this.type = type;
		this.value = value;
	}

	public int getValueAsInt() {
		return (Integer) value;
	}

	public boolean getValueAsBool() {
		return (Boolean) value;
	}

	public char getValueAsCharacter() {
		return (Character) value;
	}

	public Expression getValueAsExpression() {
		return (Expression) value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValueAsObject() {
		return value;
	}

	@Override
	public String toString() {
		return "{type=" + type + ", value=" + value + "}";
	}
}
