package model.Expression;

import antlr.ExprBaseVisitor;
import antlr.ExprParser;
import antlr.ExprParser.*;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class AntlrToExpression extends ExprBaseVisitor<Expression> {

	/*
	 * Given that all visit_* methods are called in a top-down fashion. We can be
	 * sure that the order in which we add declared variables in the `vars` is
	 * identical to how they are declared in the input program.
	 */
	public Map<String, ReturnType> vars; // stores all the variables declared in the program so far
	public List<String> semanticErrors; // 1. duplicate declaration 2. reference to undeclared variable

	public AntlrToExpression(List<String> semanticErrors) {
		this.vars = new HashMap<>();
		this.semanticErrors = semanticErrors;
	}

	/**
	 * Class level statements
	 */
	@Override
	public Expression visitClassDeclaration(ClassDeclarationContext ctx) {
		String className = ctx.getChild(1).getText();
		String superClass;
		ClassDeclaration cd;
		int startOfExpressions;

		// has a superclass
		if (ctx.ID().size() >= 2) {
			superClass = ctx.ID(1).getText();
			cd = new ClassDeclaration(className, superClass);
			startOfExpressions = 5;
		} else {
			cd = new ClassDeclaration(className);
			startOfExpressions = 3;
		}
		for (int i = startOfExpressions; i < ctx.getChildCount() - 1; i++) {
			cd.addExpression(visit(ctx.getChild(i)));
		}
		System.out.println(cd);
		return cd;
	}

	@Override
	public Expression visitDeclarationWithOptionalAssignment(DeclarationWithOptionalAssignmentContext ctx) {
		String var = ctx.getChild(0).getText();
		String type = ctx.type().getText();
		ReturnType varType = type.equals("BOOL") ? ReturnType.BOOL : ReturnType.INT;

		// redecleard var error
		if (vars.keySet().contains(var)) {
			// add semantic error redeclared var @ line, col
			Token id = ctx.ID().getSymbol();
			int line = id.getLine();
			int col = id.getCharPositionInLine() + 1;
			semanticErrors.add("Variable " + var + " already declared,  line=" + line + " col=" + col);
		} else {
			vars.put(var, varType);
		}
		if (ctx.getChildCount() > 4) {
			Expression expr = visit(ctx.expr());
			return new Declaration(var, varType, expr);
		} else {
			return new Declaration(var, varType);
		}
	}

	@Override
	public Expression visitVariableAssignment(VariableAssignmentContext ctx) {
		// TODO Auto-generated method stub
		String var = ctx.getChild(0).getText();
		Expression expr = visit(ctx.getChild(2));
		// get all vars in expr and make sure matching with var.
//		System.out.println(expr.getVariables());
		return new Assignment(var, expr);
	}

	@Override
	public Expression visitType(TypeContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("Type visitor method not yet implemented.");
		return super.visitType(ctx);
	}

	/**
	 * Binary expressions
	 */
	@Override
	public Expression visitIfStatement(IfStatementContext ctx) {
		Expression cond = visit(ctx.getChild(2));
		IfStatement ifs = new IfStatement(cond);
		for (int i = 5; i < ctx.getChildCount() - 1; i++) {
			ifs.addExpression(visit(ctx.getChild(i)));
		}
		return ifs;
	}

	@Override
	public Expression visitMultiplication(MultiplicationContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Multiplication(left, right);
	}

	@Override
	public Expression visitAddition(AdditionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Addition(left, right);
	}

	@Override
	public Expression visitLessThan(LessThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new LessThan(left, right);
	}

	@Override
	public Expression visitSubtraction(SubtractionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Subtraction(left, right);
	}

	@Override
	public Expression visitGreaterThan(GreaterThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new GreaterThan(left, right);
	}

	@Override
	public Expression visitEqual(EqualContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.EQUALITY, left, right, ctx.getStart().getLine());
		return new Equal(left, right);
	}

	@Override
	public Expression visitGreaterEqualThan(GreaterEqualThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new GreaterEqualThan(left, right);
	}

	@Override
	public Expression visitAnd(AndContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.LOGICAL, left, right, ctx.getStart().getLine());
		return new And(left, right);
	}

	@Override
	public Expression visitLessEqualThan(LessEqualThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new LessEqualThan(left, right);
	}

	@Override
	public Expression visitDivision(DivisionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Division(left, right);
	}

	@Override
	public Expression visitNotEqual(NotEqualContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.EQUALITY, left, right, ctx.getStart().getLine());
		return new NotEqual(left, right);
	}

	@Override
	public Expression visitOr(OrContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		// left and rightevals for opening up parehtnesis expressions.
		// to be valid, left and right must be relational or logical expressions.
		checkIfArgsValidBinary(ExprType.LOGICAL, left, right, ctx.getStart().getLine());
		return new Or(left, right);
	}

	@Override
	public Expression visitModulo(ModuloContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Modulo(left, right);
	}

	/**
	 * Unary Expressions
	 */
	@Override
	public Expression visitNot(NotContext ctx) {
		Expression expr = visit(ctx.getChild(1));
		Expression inner = (expr instanceof Parenthesis) ? ((Parenthesis) expr).expr : expr;
		if (inner.getReturnType() != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at line " + ctx.getStart().getLine()
					+ ": expected ( BOOL ) but got ( " + inner.getReturnType() + " )");
		}
		return new Not(expr);
	}

	@Override
	public Expression visitParenthesis(ParenthesisContext ctx) {
		Expression expr = visit(ctx.getChild(1));
		return new Parenthesis(expr);
	}

	/**
	 * Variables, Literals
	 */

	@Override
	public Expression visitVariable(VariableContext ctx) {
		String var = ctx.getChild(0).getText();
		Token id = ctx.ID().getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;

		// check if variable is declared
		if (!this.vars.containsKey(var)) {
			semanticErrors.add("Variable '" + var + "' not declared, line=" + line + " col=" + col);
		}

		ReturnType returnType = vars.get(var);
		return new Variable(var, returnType);
	}

	@Override
	public Expression visitBooleanLiteral(BooleanLiteralContext ctx) {
		String value = ctx.getText();
		return new BooleanLiteral(Boolean.parseBoolean(value));
	}

	@Override
	public Expression visitNumberLiteral(NumberLiteralContext ctx) {
		String value = ctx.getText();
		return new NumberLiteral(Integer.parseInt(value));
	}

	private void checkIfArgsValidBinary(ExprType exprType, Expression left, Expression right, int lineNum) {
		boolean isValid = true;

		Expression leftEval = left instanceof Parenthesis ? ((Parenthesis) left).expr : left;
		Expression rightEval = right instanceof Parenthesis ? ((Parenthesis) right).expr : right;
		ReturnType leftReturnType = leftEval.getReturnType();
		ReturnType rightReturnType = rightEval.getReturnType();

		String typeOfExpression = "";
		String expectedTypes = "";
		String actualTypes = "(" + leftReturnType + ", " + rightReturnType + ")";

		if (exprType == ExprType.LOGICAL) {
			typeOfExpression = "logical";
			expectedTypes = "(BOOL, BOOL)";
			isValid = leftReturnType == ReturnType.BOOL && rightReturnType == ReturnType.BOOL;

		} else if (exprType == ExprType.ARITHMETIC) {
			typeOfExpression = "arithmetic";
			expectedTypes = "(INT, INT)";
			isValid = leftReturnType == ReturnType.INT && rightReturnType == ReturnType.INT;

		} else if (exprType == ExprType.RELATIONAL) {
			typeOfExpression = "relational";
			expectedTypes = "(INT, INT)";
			isValid = leftReturnType == ReturnType.INT && rightReturnType == ReturnType.INT;

		} else if (exprType == ExprType.EQUALITY) {
			typeOfExpression = "equality";
			expectedTypes = "(INT, INT) or (BOOL, BOOL)";
			isValid = (leftReturnType == rightReturnType)
					&& (leftReturnType == ReturnType.INT || leftReturnType == ReturnType.BOOL);
		}

		if (!isValid) {
			semanticErrors.add("Type mismatch in " + typeOfExpression + " expression at line " + lineNum + ": expected "
					+ expectedTypes + " but got " + actualTypes + "");

		}
	}

}