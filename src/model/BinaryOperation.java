package model;

public class BinaryOperation extends Expression {
    public Expression left;
    public String operator;
    public Expression right;

    public BinaryOperation(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }
}