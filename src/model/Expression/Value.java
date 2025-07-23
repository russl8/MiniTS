package model.Expression;

import model.Expression.Expression.PrimitiveType;

public class Value {
	public PrimitiveType type;
	private Object value;

	public Value(PrimitiveType type) {
		this.type = type;
		this.value = null;
	}

	public Value(PrimitiveType type, Object value) {
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

	public void setValue(Value value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{type=" + type + ", value=" + value + "}";
	}
}
