package model.Expression;

import antlr.ExprBaseVisitor;
import antlr.ExprParser.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.Token;

public class AntlrToExpression extends ExprBaseVisitor<Expression> {

	/*
	 * Given that all visit_* methods are called in a top-down fashion. We can be
	 * sure that the order in which we add declared variables in the `vars` is
	 * identical to how they are declared in the input program.
	 */
	private List<String> vars; // stores all the variables declared in the program so far
	private List<String> semanticErrors; // 1. duplicate declaration 2. reference to undeclared variable

	public AntlrToExpression(List<String> semanticErrors) {
		this.vars = new ArrayList<>();
		this.semanticErrors = semanticErrors;
	}

	/**
	 * Class level statements
	 */
	@Override
	public Expression visitClassDeclaration(ClassDeclarationContext ctx) {
		String className = ctx.getChild(1).getText();
		String superClass;
		Expression cd;
		if (ctx.ID().size() >= 2) {
			superClass = ctx.ID(1).getText();
			cd = new ClassDeclaration(className, superClass);
		} else {
			cd = new ClassDeclaration(className);
		}
		return cd;
	}

	@Override
	public Expression visitDeclarationWithOptionalAssignment(DeclarationWithOptionalAssignmentContext ctx) {
		// TODO Auto-generated method stub
		return super.visitDeclarationWithOptionalAssignment(ctx);
	}

	@Override
	public Expression visitVariableAssignment(VariableAssignmentContext ctx) {
		// TODO Auto-generated method stub
		return super.visitVariableAssignment(ctx);
	}

	@Override
	public Expression visitType(TypeContext ctx) {
		// TODO Auto-generated method stub
		return super.visitType(ctx);
	}

	/**
	 * Binary expressions
	 */
	@Override
	public Expression visitIfStatement(IfStatementContext ctx) {
		// TODO Auto-generated method stub
		return super.visitIfStatement(ctx);
	}

	@Override
	public Expression visitMultiplication(MultiplicationContext ctx) {
		// TODO Auto-generated method stub
		return super.visitMultiplication(ctx);
	}

	@Override
	public Expression visitAddition(AdditionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAddition(ctx);
	}

	@Override
	public Expression visitLessThan(LessThanContext ctx) {
		// TODO Auto-generated method stub
		return super.visitLessThan(ctx);
	}

	@Override
	public Expression visitSubtraction(SubtractionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitSubtraction(ctx);
	}

	@Override
	public Expression visitGreaterThan(GreaterThanContext ctx) {
		// TODO Auto-generated method stub
		return super.visitGreaterThan(ctx);
	}

	@Override
	public Expression visitEqual(EqualContext ctx) {
		// TODO Auto-generated method stub
		return super.visitEqual(ctx);
	}

	@Override
	public Expression visitGreaterEqualThan(GreaterEqualThanContext ctx) {
		// TODO Auto-generated method stub
		return super.visitGreaterEqualThan(ctx);
	}

	@Override
	public Expression visitAnd(AndContext ctx) {
		// TODO Auto-generated method stub
		return super.visitAnd(ctx);
	}

	@Override
	public Expression visitLessEqualThan(LessEqualThanContext ctx) {
		// TODO Auto-generated method stub
		return super.visitLessEqualThan(ctx);
	}

	@Override
	public Expression visitDivision(DivisionContext ctx) {
		// TODO Auto-generated method stub
		return super.visitDivision(ctx);
	}

	@Override
	public Expression visitNotEqual(NotEqualContext ctx) {
		// TODO Auto-generated method stub
		return super.visitNotEqual(ctx);
	}

	@Override
	public Expression visitOr(OrContext ctx) {
		// TODO Auto-generated method stub
		return super.visitOr(ctx);
	}

	@Override
	public Expression visitModulo(ModuloContext ctx) {
		// TODO Auto-generated method stub
		return super.visitModulo(ctx);
	}

	/**
	 * Unary Expressions
	 */
	@Override
	public Expression visitNot(NotContext ctx) {
		// TODO Auto-generated method stub
		return super.visitNot(ctx);
	}

	@Override
	public Expression visitParenthesis(ParenthesisContext ctx) {
		// TODO Auto-generated method stub
		return super.visitParenthesis(ctx);
	}

	/**
	 * Variables, Literals
	 */

	@Override
	public Expression visitVariable(VariableContext ctx) {
		// TODO Auto-generated method stub
		return super.visitVariable(ctx);
	}

	@Override
	public Expression visitBooleanLiteral(BooleanLiteralContext ctx) {
		// TODO Auto-generated method stub
		return super.visitBooleanLiteral(ctx);
	}

	@Override
	public Expression visitNumberLiteral(NumberLiteralContext ctx) {
		// TODO Auto-generated method stub
		return super.visitNumberLiteral(ctx);
	}

}