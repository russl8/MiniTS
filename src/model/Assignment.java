package model;

public class Assignment extends Expression {
    public String variable;
    public Expression value;

    public Assignment(String variable, Expression value) {
        this.variable = variable;
        this.value = value;
    }
}