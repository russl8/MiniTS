package model;

import model.Number;  // your custom Number class
import model.BooleanVal; // your custom Boolean class (assumed to be created like Number)
import model.Expression;

public class BinaryOperation extends Expression{
    private final Expression result;
    public int left;
    public int right;
    public String operator;
    // Constructor for numeric expressions
    public BinaryOperation(Number left, String operator, Number right) {
        int l = left.value;
        int r = right.value;
        
        this.left = l;
        this.right = r;
        this.operator = operator;

        switch (operator) {
            case "+": result = new Number(l + r); break;
            case "-": result = new Number(l - r); break;
            case "*": result = new Number(l * r); break;
            case "/":
                if (r == 0) throw new RuntimeException("Division by zero");
                result = new Number(l / r);
                break;
            case "%": result = new Number(l % r); break;
            case "==": result = new BooleanVal(l == r); break;
            case "!=": result = new BooleanVal(l != r); break;
            case "<":  result = new BooleanVal(l < r); break;
            case ">":  result = new BooleanVal(l > r); break;
            case "<=": result = new BooleanVal(l <= r); break;
            case ">=": result = new BooleanVal(l >= r); break;
            default:
                throw new RuntimeException("Unsupported operator for numbers: " + operator);
        }
    }

    // Constructor for boolean expressions
    public BinaryOperation(BooleanVal left, String operator, BooleanVal right) {
        boolean l = left.value;
        boolean r = right.value;

        switch (operator) {
            case "&&": result = new BooleanVal(l && r); break;
            case "||": result = new BooleanVal(l || r); break;
            case "==": result = new BooleanVal(l == r); break;
            case "!=": result = new BooleanVal(l != r); break;
            default:
                throw new RuntimeException("Unsupported operator for booleans: " + operator);
        }
    }

    // Returns the result of the operation
    public Expression evaluate() {
        return result;
    }
}