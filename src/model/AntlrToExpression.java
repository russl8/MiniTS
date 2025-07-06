package model;

import antlr.ExprBaseVisitor;
import antlr.ExprParser.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

public class AntlrToExpression extends ExprBaseVisitor<Expression> {

    /*
     * Given that all visit_* methods are called in a top-down fashion.
     * We can be sure that the order in which we add declared variables in the `vars` is
     * identical to how they are declared in the input program.
     */
    private List<String> vars; // stores all the variables declared in the program so far
    private List<String> semanticErrors; // 1. duplicate declaration 2. reference to undeclared variable
    // Note that semantic errors are different from syntax errors.
    private String currentOperation; // track current operation being processed
    Map<String, Variable> variables = new HashMap<>();
    
    public AntlrToExpression(List<String> semanticErrors) {
        this.vars = new ArrayList<>();
        this.semanticErrors = semanticErrors;
        this.currentOperation = null;
    }

    @Override
    public Expression visitClassDeclaration(ClassDeclarationContext ctx) {
        String className = ctx.ID(0).getText();
        System.out.println("Visiting class: " + className);
        
        if (ctx.ID().size() > 1) {
            String parentClass = ctx.ID(1).getText();
            System.out.println("Extends: " + parentClass);
        }
        
        return visitChildren(ctx);
    }

    @Override
    public Expression visitDeclarationWithOptionalAssignment(DeclarationWithOptionalAssignmentContext ctx) {
        String name = ctx.ID().getText();
        String type = ctx.type().getText();  // "INT" or "BOOL"

        if (variables.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' already declared.");
        }

        Variable var;
        if (ctx.expr() != null) {
            Object value = visit(ctx.expr());
            var = new Variable(name, type, value);
        } else {
            var = new Variable(type);
        }

        variables.put(name, var);
        System.out.println("Declared " + name + ": " + var);
        return var;
    }

    @Override
    public Expression visitVariableAssignment(VariableAssignmentContext ctx) {
    	String name = ctx.ID().getText();

        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' not declared.");
        }

        Variable var = variables.get(name);

        if (ctx.expr() == null) {
            throw new RuntimeException("No value provided for variable assignment.");
        }

        Object value = visit(ctx.expr());

        // Update variable value with type checking inside Variable.setValue
        var.setValue(value);

        System.out.println("Assigned " + name + " = " + value);

        return var; // or return value if you want
    }

    @Override
    public Expression visitIfStatement(IfStatementContext ctx) {
        System.out.println("Evaluating IF condition:");
        Expression val = visit(ctx.expr());
        if (val instanceof BooleanVal) {
        	if (((BooleanVal)val).value) {
        		System.out.println("Entering IF block:");
        		for (StatementContext stmt : ctx.statement()) {
        			visit(stmt);
        		}
        	}
        }
        return null;
    }
    
    @Override
    public Expression visitBinaryExpr(BinaryExprContext ctx) {
        Expression left = visit(ctx.expr(0));
        Expression right = visit(ctx.expr(1));
        String op = ctx.op.getText();

        if (left instanceof Number && right instanceof Number) {
            return new BinaryOperation((Number) left, op, (Number) right).evaluate();
        } else if (left instanceof BooleanVal && right instanceof BooleanVal) {
            return new BinaryOperation((BooleanVal) left, op, (BooleanVal) right).evaluate();
        } else {
            throw new RuntimeException("Invalid operand types for operator '" + op + "'");
        }
    }
	
    @Override
    public Expression visitVariable(VariableContext ctx) {
        String name = ctx.ID().getText();

        if (!variables.containsKey(name)) {
            throw new RuntimeException("Variable '" + name + "' not declared.");
        }

        Variable var = variables.get(name);
        Expression value = var.value;

        if (value == null) {
            throw new RuntimeException("Variable '" + name + "' has not been assigned a value.");
        }

        System.out.println("Variable lookup: " + name + " = " + value);
        return value;
    }

    @Override
    public Expression visitBooleanLiteral(BooleanLiteralContext ctx) {
        boolean value = Boolean.parseBoolean(ctx.BOOL().getText());
        return new BooleanVal(value); // fully-qualified if name clash with java.lang.Boolean
    }

    @Override
    public Expression visitNumberLiteral(NumberLiteralContext ctx) {
        int value = Integer.parseInt(ctx.NUM().getText());
        return new Number(value); // your model.Number
    }
}