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
import model.Expression.Declaration.ClassDeclaration;
import model.Expression.Declaration.ListDeclaration;
import model.Expression.Declaration.PrimitaveDeclaration;
import model.Expression.List.ListLiteral;
import model.Value;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Expression.Statement.PrimitiveAssignment;
import model.Expression.Statement.WhileLoop;
import model.Expression.Statement.IfStatement;
import model.Expression.Statement.ListAssignment;
import model.Expression.Statement.PrimitiveAssignment;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class AntlrToExpression extends ExprBaseVisitor<Expression> {

	/*
	 * Given that all visit_* methods are called in a top-down fashion. We can be
	 * sure that the order in which we add declared variables in the `vars` is
	 * identical to how they are declared in the input program.
	 */
	public Map<String, Type> vars; // stores all the variables declared in the program so far
	public List<String> semanticErrors; // 1. duplicate declaration 2. reference to undeclared variable

	public AntlrToExpression(List<String> semanticErrors, Map<String, Type> vars) {
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
		Map<String, Type> primitaveTypes = Map.of("bool", Type.BOOL, "int", Type.INT, "char", Type.CHAR);
		String var = ctx.getChild(0).getText();
		TypeContext declType = ctx.type();
		Token id = ctx.ID().getSymbol();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;
		boolean isInitialized = ctx.getChildCount() > 4;

		/**
		 * Primitave type declaration (ex: int, bool, char)
		 */
		if (primitaveTypes.containsKey(declType.getText())) {
			Type varType = primitaveTypes.get(declType.getText());

			if (vars.keySet().contains(var)) {
				semanticErrors.add("Variable " + var + " already declared,  line=" + line + " col=" + col);
			} else {
				vars.put(var, varType);
			}
			// expr is initialized
			if (isInitialized) {
				Expression expr = visit(ctx.expr());
				return new PrimitaveDeclaration(var, varType, expr, line, col);
			} else {
				return new PrimitaveDeclaration(var, varType, line, col);
			}
		}
		/**
		 * Non-primitave type declaration (ex: list[char], map[char, char])
		 */
		else {
			String objectType = declType.getChild(0).getText();
			// Var already declared error
			if (vars.keySet().contains(var)) {
				semanticErrors.add("Variable " + var + " already declared,  line=" + line + " col=" + col);
			} else {
				vars.put(var, Type.NONE);
			}

			if (objectType.equals("list")) {
				String itemType = declType.getChild(2).getText();
				Expression ld;
				Type listType;

				switch (itemType) {
				case "int":
					listType = Type.LIST_INT;
					break;
				case "bool":
					listType = Type.LIST_BOOL;
					break;
				case "char":
					listType = Type.LIST_CHAR;
					break;
				default:
					listType = Type.NONE;
					System.out.println("Unexpected type: " + itemType);
				}

				if (isInitialized) {
					// Populate list
					Expression uncheckedListLiteral = visit(ctx.getChild(4));
					ld = new ListDeclaration(var, uncheckedListLiteral, listType, line, col);
				} else {
					ld = new ListDeclaration(var, listType, line, col);
				}
				this.vars.put(var, listType);

				return ld;
			} else {
				System.err.println("Declaration with unknown object type: " + ctx.getText());
			}
		}
		System.err.println("Could not process declaration " + ctx.getText());
		return null;
	}

	@Override
	public Expression visitVariableAssignment(VariableAssignmentContext ctx) {
		/**
		 * TODO: separate primitave vs object (eg.list) assignments
		 */
		String var = ctx.getChild(0).getText();
		Expression expr = visit(ctx.getChild(2));
		Token id = ctx.ID().getSymbol();
		int lineNum = id.getLine();
		int colNum = id.getCharPositionInLine() + 1;

		if (!this.vars.containsKey(var)) {
			semanticErrors.add("Assignment to an undeclared variable in [" + lineNum + ", " + colNum + "]: " + var);
		}
		if (expr.getReturnType() != Type.NONE) {
			return new PrimitiveAssignment(var, expr, lineNum, colNum);
		} else if (expr instanceof ListLiteral) {
			return new ListAssignment(var, expr, lineNum, colNum);
		}
		System.err.println("AntlrToExpression: Unexpected expression: " + ctx.getText());
		return null;
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
	public Expression visitWhileLoop(WhileLoopContext ctx) {
		/*
		 * get line, col, condition, create whileloop object WL for each statement in
		 * the while loop, WL.addExpression(statement) return WL
		 */
		Expression cond = visit(ctx.getChild(2));
		int line = ctx.expr().getStart().getLine();
		int col = ctx.expr().getStart().getCharPositionInLine() + 1;
		WhileLoop wl = new WhileLoop(cond, line, col);

		for (int i = 5; i < ctx.getChildCount() - 1; i++) {
			wl.addExpression(visit(ctx.getChild(i)));
		}
		return wl;
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

		/**
		 * Get var name, make sure not declared get type, make sure it is valid
		 * 
		 * iterate through ctx.expr[1:]. visit those and add them to the List
		 */
		Token id = ctx.getStart();
		int line = id.getLine();
		int col = id.getCharPositionInLine() + 1;

		ListLiteral ll = new ListLiteral(line, col);

		boolean isNonEmptyInitialization = ctx.getChildCount() > 2;

		for (int i = 1; isNonEmptyInitialization && i < ctx.getChildCount(); i += 2) {
			ll.add(visit(ctx.getChild(i)));
		}
		return ll;
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

		Type type = vars.get(var);
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