package model.Expression;

import antlr.ExprBaseVisitor;
import antlr.ExprParser;
import antlr.ExprParser.*;
import model.Expression.Binary.Addition;
import model.Expression.Binary.And;
import model.Expression.Binary.Division;
import model.Expression.Binary.Equal;
import model.Expression.Binary.GreaterEqualThan;
import model.Expression.Binary.GreaterThan;
import model.Expression.Binary.LessEqualThan;
import model.Expression.Binary.LessThan;
import model.Expression.Binary.Modulo;
import model.Expression.Binary.Multiplication;
import model.Expression.Binary.NotEqual;
import model.Expression.Binary.Or;
import model.Expression.Binary.Subtraction;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.Statement.Assignment;
import model.Expression.Statement.ClassDeclaration;
import model.Expression.Statement.Declaration;
import model.Expression.Statement.IfStatement;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;

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
		Token id = ctx.ID(0).getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;
		ClassDeclaration cd;
		int startOfExpressions;

		// has a superclass
		if (ctx.ID().size() >= 2) {
			superClass = ctx.ID(1).getText();
			cd = new ClassDeclaration(className, superClass, line, col);
			startOfExpressions = 5;
		} else {
			cd = new ClassDeclaration(className, line, col);
			startOfExpressions = 3;
		}
		for (int i = startOfExpressions; i < ctx.getChildCount() - 1; i++) {
			cd.addExpression(visit(ctx.getChild(i)));
		}
//		System.out.println(cd);
		return cd;
	}

	@Override
	public Expression visitDeclarationWithOptionalAssignment(DeclarationWithOptionalAssignmentContext ctx) {
		String var = ctx.getChild(0).getText();
		String type = ctx.type().getText();
		ReturnType varType = type.equals("BOOL") ? ReturnType.BOOL : ReturnType.INT;
		Token id = ctx.ID().getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;

		// redecleard var error
		if (vars.keySet().contains(var)) {
			// add semantic error redeclared var @ line, col
			semanticErrors.add("Variable " + var + " already declared,  line=" + line + " col=" + col);
		} else {
			vars.put(var, varType);
		}
		// expr is initialized
		if (ctx.getChildCount() > 4) {
			Expression expr = visit(ctx.expr());
			// initializing type error
			if (varType != expr.getReturnType()) {
				semanticErrors.add("Type mismatch at line " + line + ": expected " + varType + " = " + varType
						+ " assignment but got " + varType + " = " + expr.getReturnType());
			}
			return new Declaration(var, varType, expr, line, col);
		} else {
			return new Declaration(var, varType, line, col);
		}
	}

	@Override
	public Expression visitVariableAssignment(VariableAssignmentContext ctx) {
		String var = ctx.getChild(0).getText();
		Expression expr = visit(ctx.getChild(2));
		Token id = ctx.ID().getSymbol();
		int lineNum = id.getLine();
		int colNum = id.getCharPositionInLine() + 1;
		// open up parenthesis to get the inside expr
		Expression cleanedExpr = Utils.unwrapParentheses(expr);

		ReturnType exprType = cleanedExpr.getReturnType();
		// make sure that the variable is being assigned properly
		// (int -> int, bool -> bool)
		if (this.vars.get(var) == ReturnType.BOOL && exprType != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch at line " + lineNum + ": expected BOOL = BOOL assignment but got BOOL = "
					+ exprType);
		} else if (this.vars.get(var) == ReturnType.INT && exprType != ReturnType.INT) {
			semanticErrors.add(
					"Type mismatch at line " + lineNum + ": expected INT = INT assignment but got INT = " + exprType);
		}

		return new Assignment(var, expr, lineNum, colNum);
	}

	@Override
	public Expression visitType(TypeContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("Type visitor method not yet implemented.");
		return super.visitType(ctx);
	}

	@Override
	public Expression visitIfStatement(IfStatementContext ctx) {
		Expression cond = visit(ctx.getChild(2));
		int line = ctx.expr().getStart().getLine();
		int col = ctx.expr().getStart().getCharPositionInLine() + 1;

		// cond must be a boolean expression
		if (cond.getReturnType() != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at line " + ctx.getStart().getLine()
					+ ": expected ( BOOL ) but got ( " + cond.getReturnType() + " )");
		}
		IfStatement ifs = new IfStatement(cond, line, col);
		for (int i = 5; i < ctx.getChildCount() - 1; i++) {
			ifs.addExpression(visit(ctx.getChild(i)));
		}
		return ifs;
	}

	@Override
	public Expression visitMultiplication(MultiplicationContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Multiplication(left, right);
	}

	@Override
	public Expression visitAddition(AdditionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Addition(left, right);
	}

	@Override
	public Expression visitLessThan(LessThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new LessThan(left, right);
	}

	@Override
	public Expression visitSubtraction(SubtractionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Subtraction(left, right);
	}

	@Override
	public Expression visitGreaterThan(GreaterThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new GreaterThan(left, right);
	}

	@Override
	public Expression visitEqual(EqualContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.EQUALITY, left, right, ctx.getStart().getLine());
		return new Equal(left, right);
	}

	@Override
	public Expression visitGreaterEqualThan(GreaterEqualThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new GreaterEqualThan(left, right);
	}

	@Override
	public Expression visitAnd(AndContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.LOGICAL, left, right, ctx.getStart().getLine());
		return new And(left, right);
	}

	@Override
	public Expression visitLessEqualThan(LessEqualThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.RELATIONAL, left, right, ctx.getStart().getLine());
		return new LessEqualThan(left, right);
	}

	@Override
	public Expression visitDivision(DivisionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Division(left, right);
	}

	@Override
	public Expression visitNotEqual(NotEqualContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.EQUALITY, left, right, ctx.getStart().getLine());
		return new NotEqual(left, right);
	}

	@Override
	public Expression visitOr(OrContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.LOGICAL, left, right, ctx.getStart().getLine());
		return new Or(left, right);
	}

	@Override
	public Expression visitModulo(ModuloContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		checkIfExprArgsValidBinary(ExprType.ARITHMETIC, left, right, ctx.getStart().getLine());
		return new Modulo(left, right);
	}

	/**
	 * Unary Expressions
	 */
	@Override
	public Expression visitNot(NotContext ctx) {
		Expression expr = visit(ctx.getChild(1));
		Expression exprInsideParenthesis = Utils.unwrapParentheses(expr);
		if (exprInsideParenthesis.getReturnType() != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at line " + ctx.getStart().getLine()
					+ ": expected ( BOOL ) but got ( " + exprInsideParenthesis.getReturnType() + " )");
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
		return new Variable(var, returnType, line, col);
	}

	@Override
	public Expression visitBooleanLiteral(BooleanLiteralContext ctx) {
		String value = ctx.getText();
		Token id = ctx.BOOL().getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;
		return new BooleanLiteral(Boolean.parseBoolean(value), line, col);
	}

	@Override
	public Expression visitNumberLiteral(NumberLiteralContext ctx) {
		String value = ctx.getText();
		Token id = ctx.NUM().getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;
		return new NumberLiteral(Integer.parseInt(value), line, col);
	}

	/**
	 * Given an binary expression's type, type-check the expression's left and right
	 * hand arguments. On failure, append to semantic errors.
	 * 
	 * @param exprType
	 * @param left
	 * @param right
	 * @param lineNum
	 */
	private void checkIfExprArgsValidBinary(ExprType exprType, Expression left, Expression right, int lineNum) {
		boolean isValid = true;

		int line = left.getLine();
		int col = left.getCol();

		Expression unwrappedLeft = Utils.unwrapParentheses(left);
		Expression unwrappedRight = Utils.unwrapParentheses(right);

		ReturnType leftReturnType = unwrappedLeft.getReturnType();
		ReturnType rightReturnType = unwrappedRight.getReturnType();

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
			semanticErrors.add("Type mismatch in " + typeOfExpression + " expression at [" + line + ", " + col
					+ "]: expected " + expectedTypes + " but got " + actualTypes + "");

		}
	}

}