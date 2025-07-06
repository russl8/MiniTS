package model;

public class Variable extends Expression{
	public String name;
	public final String type;  // "INT" or "BOOL"
	public Expression value;

    public Variable(String type) {
        this.type = type;
        this.value = null;
    }

    public Variable(String name, String type, Object value) {
    	this.name = name;
        this.type = type;
        setValue(value);
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object newValue) {
        if (!isCorrectType(newValue)) {
            throw new RuntimeException("Type mismatch: expected " + type + ", got " +
                (newValue == null ? "null" : newValue.getClass().getSimpleName()));
        }
        if ("INT".equals(type)) {
        	this.value = (Number) newValue;
        } else if ("BOOL".equals(type)) {
        	this.value = (BooleanVal) newValue;
        }
    }

    private boolean isCorrectType(Object value) {
        if ("INT".equals(type)) {
            return value instanceof Number;
        } else if ("BOOL".equals(type)) {
            return value instanceof BooleanVal;
        }
        return false;
    }

    @Override
    public String toString() {
    	if (value != null ) {
    		return "Variable{type=" + type + ", value=" + value.toString() + "}";
    	} else {
    		return "Variable{type=" + type + "}";
    	}
    }
}