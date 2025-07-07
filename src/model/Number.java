package model;

public class Number extends Expression {
    public int value;

    public Number(int value) {
        this.value = value;
    }
    
    public int toInt() {
    	return this.value;
    }
    
    public String toString() {
    	return String.valueOf(value);
    }
}