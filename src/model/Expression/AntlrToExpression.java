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
import model.Expression.List.ListLiteral;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.PrimitiveType;
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
	public Map<String, PrimitiveType> vars; // stores all the variables declared in the program so far
	public List<String> semanticErrors; // 1. duplicate declaration 2. reference to undeclared variable

	public AntlrToExpression(List<String> semanticErrors, Map<String, PrimitiveType> vars) {
		this.vars = vars;
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
		Map<String, PrimitiveType> varTypes = Map.of("bool", PrimitiveType.BOOL, "int", PrimitiveType.INT, "char",
				PrimitiveType.CHAR);
		String var = ctx.getChild(0).getText();
		String type = ctx.type().getText();

		// get variable type
		PrimitiveType varType = varTypes.get(ctx.getChild(2).getText());

		// get line and col
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
		return new Multiplication(left, right);
	}

	@Override
	public Expression visitAddition(AdditionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new Addition(left, right);
	}

	@Override
	public Expression visitLessThan(LessThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new LessThan(left, right);
	}

	@Override
	public Expression visitSubtraction(SubtractionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new Subtraction(left, right);
	}

	@Override
	public Expression visitGreaterThan(GreaterThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new GreaterThan(left, right);
	}

	@Override
	public Expression visitEqual(EqualContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new Equal(left, right);
	}

	@Override
	public Expression visitGreaterEqualThan(GreaterEqualThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new GreaterEqualThan(left, right);
	}

	@Override
	public Expression visitAnd(AndContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new And(left, right);
	}

	@Override
	public Expression visitLessEqualThan(LessEqualThanContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new LessEqualThan(left, right);
	}

	@Override
	public Expression visitDivision(DivisionContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new Division(left, right);
	}

	@Override
	public Expression visitNotEqual(NotEqualContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new NotEqual(left, right);
	}

	@Override
	public Expression visitOr(OrContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new Or(left, right);
	}

	@Override
	public Expression visitModulo(ModuloContext ctx) {
		Expression left = visit(ctx.getChild(0));
		Expression right = visit(ctx.getChild(2));
		return new Modulo(left, right);
	}

	/**
	 * Unary Expressions
	 */
	@Override
	public Expression visitNot(NotContext ctx) {
		Expression expr = visit(ctx.getChild(1));
//		Expression exprInsideParenthesis = Utils.unwrapParentheses(expr);

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
	public Expression visitListLiteral(ListLiteralContext ctx) {
		
		
		return new ListLiteral();
	}


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

		PrimitiveType type = vars.get(var);
		return new Variable(var, type, line, col);
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

	@Override
	public Expression visitCharacterLiteral(CharacterLiteralContext ctx) {
		String value = ctx.getText();
		Token id = ctx.CHAR().getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;
		return new CharacterLiteral(value.charAt(1), line, col);
	}

}