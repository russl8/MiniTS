package model;

import antlr.ExprBaseVisitor;
import antlr.ExprParser;

import java.util.ArrayList;
import java.util.List;

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

    public AntlrToExpression(List<String> semanticErrors) {
        this.vars = new ArrayList<>();
        this.semanticErrors = semanticErrors;
        this.currentOperation = null;
    }

    @Override
    public Expression visitClassDeclaration(ExprParser.ClassDeclarationContext ctx) {
        ClassDeclaration classDecl = new ClassDeclaration();
        classDecl.className = ctx.ID().getText();

        // Visit attributes section to collect attributes
        if (ctx.attributes_section() != null) {
            ExprParser.AttributesSectionContext attrsSection = (ExprParser.AttributesSectionContext) ctx.attributes_section();
            for (ExprParser.AttributeContext attrCtx : attrsSection.attribute()) {
                AttributeDeclaration attr = (AttributeDeclaration) visit(attrCtx);
                if (attr != null) {
                    classDecl.addAttribute(attr);
                }
            }
        }

        // Visit operations section to collect operations
        if (ctx.operations_section() != null) {
            ExprParser.OperationsSectionContext opsSection = (ExprParser.OperationsSectionContext) ctx.operations_section();
            for (ExprParser.OperationContext opCtx : opsSection.operation()) {
                OperationDeclaration op = (OperationDeclaration) visit(opCtx);
                if (op != null) {
                    classDecl.addOperation(op);
                }
            }
        }

        return classDecl;
    }

    @Override
    public Expression visitAttributesSection(ExprParser.AttributesSectionContext ctx) {
        // This method is called for structure but doesn't return a meaningful Expression
        return null;
    }

    @Override
    public Expression visitAttributeDeclaration(ExprParser.AttributeDeclarationContext ctx) {
        String attrName = ctx.ID().getText();
        String attrType = ctx.INT_TYPE().getText();

        // Add to declared variables
        vars.add(attrName);

        return new AttributeDeclaration(attrName, attrType);
    }

    @Override
    public Expression visitOperationsSection(ExprParser.OperationsSectionContext ctx) {
        // This method is called for structure but doesn't return a meaningful Expression
        return null;
    }

    @Override
    public Expression visitOperationDeclaration(ExprParser.OperationDeclarationContext ctx) {
        String opName = ctx.ID().getText();
        currentOperation = opName; // Set current operation context
        OperationDeclaration op = new OperationDeclaration(opName);

        // Visit do block to collect assignments
        if (ctx.do_block() != null) {
            ExprParser.DoBlockContext doBlock = (ExprParser.DoBlockContext) ctx.do_block();
            for (ExprParser.AssignmentContext assignCtx : doBlock.assignment()) {
                Assignment assign = (Assignment) visit(assignCtx);
                if (assign != null) {
                    op.addAssignment(assign);
                }
            }
        }

        currentOperation = null; // Clear operation context
        return op;
    }

    @Override
    public Expression visitDoBlock(ExprParser.DoBlockContext ctx) {
        // This method is called for structure but doesn't return a meaningful Expression
        return null;
    }

    @Override
    public Expression visitAttributeAssignment(ExprParser.AttributeAssignmentContext ctx) {
        String varName = ctx.ID().getText();

        // Check if LHS variable is declared
        if (!vars.contains(varName)) {
            semanticErrors.add("Error: In `" + currentOperation + "`, `" + varName + "` on the LHS is undeclared.");
        }

        Expression value = visit(ctx.expr());
        return new Assignment(varName, value);
    }

    @Override
    public Expression visitMultiplication(ExprParser.MultiplicationContext ctx) {
        Expression left = visit(ctx.expr(0));
        Expression right = visit(ctx.expr(1));
        return new BinaryOperation(left, "*", right);
    }

    @Override
    public Expression visitAddition(ExprParser.AdditionContext ctx) {
        Expression left = visit(ctx.expr(0));
        Expression right = visit(ctx.expr(1));
        return new BinaryOperation(left, "+", right);
    }

    @Override
    public Expression visitVariable(ExprParser.VariableContext ctx) {
        String varName = ctx.ID().getText();

        // Check if RHS variable is declared
        if (!vars.contains(varName)) {
            semanticErrors.add("Error: In `" + currentOperation + "`, `" + varName + "` on the RHS is undeclared.");
        }

        return new Variable(varName);
    }

    @Override
    public Expression visitNumber(ExprParser.NumberContext ctx) {
        int value = Integer.parseInt(ctx.NUM().getText());
        return new Number(value);
    }
}