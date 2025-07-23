package model.Expression.OperationVisitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import antlr.ExprBaseVisitor;
import antlr.ExprParser.AdditionContext;
import antlr.ExprParser.AndContext;
import antlr.ExprParser.BooleanLiteralContext;
import antlr.ExprParser.ClassDeclarationContext;
import antlr.ExprParser.DeclarationWithOptionalAssignmentContext;
import antlr.ExprParser.DivisionContext;
import antlr.ExprParser.EqualContext;
import antlr.ExprParser.GreaterEqualThanContext;
import antlr.ExprParser.GreaterThanContext;
import antlr.ExprParser.IfStatementContext;
import antlr.ExprParser.LessEqualThanContext;
import antlr.ExprParser.LessThanContext;
import antlr.ExprParser.ModuloContext;
import antlr.ExprParser.MultiplicationContext;
import antlr.ExprParser.NotContext;
import antlr.ExprParser.NotEqualContext;
import antlr.ExprParser.NumberLiteralContext;
import antlr.ExprParser.OrContext;
import antlr.ExprParser.ParenthesisContext;
import antlr.ExprParser.StatementContext;
import antlr.ExprParser.SubtractionContext;
import antlr.ExprParser.TypeContext;
import antlr.ExprParser.VariableAssignmentContext;
import antlr.ExprParser.VariableContext;
import model.Expression.BooleanLiteral;
import model.Expression.CharacterLiteral;
import model.Expression.Expression;
import model.Expression.NumberLiteral;
import model.Expression.Utils;
import model.Expression.Variable;
import model.Expression.Binary.Addition;
import model.Expression.Binary.And;
import model.Expression.Binary.BinaryExpression;
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
import model.Expression.Statement.Assignment;
import model.Expression.Statement.ClassDeclaration;
import model.Expression.Statement.Declaration;
import model.Expression.Statement.IfStatement;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Program.Program;

public class ExpressionTypeChecker implements OperationVisitor {

	public List<String> semanticErrors;
	public Map<String, ReturnType> vars; // stores all the variables declared in the program so far

	public ExpressionTypeChecker(List<String> semanticErrors, Map<String, ReturnType> vars) {
		this.vars = vars;
		this.semanticErrors = semanticErrors;
	}

	@Override
	public <T> T visitClassDeclaration(ClassDeclaration cd) {
		return null;
	}

	@Override
	public <T> T visitDeclarationWithOptionalAssignment(Declaration d) {
		ReturnType varType = vars.get(d.var);
		// If declaration is initialized, typecheck its expressoin
		if (d.isInitialized) {
			ReturnType exprType = d.expr.getReturnType();
			if (varType != exprType) {
				semanticErrors.add("Type mismatch at [" + d.getLine() + ", " + d.getCol() + "]: expected " + varType
						+ " = " + varType + " assignment but got " + varType + " = " + exprType);
			}
			// recursively typecheck the inner expression
			d.expr.accept(this);
		}

		return null;
	}

	@Override
	public <T> T visitAssignment(Assignment a) {
		// TODO: Recursively visit the assignment left and right.
		// do the same for visitBinaryExpression (ex: recursively visit left and right)

		String var = a.var;
		// open up parenthesis to get the inside expr
		ReturnType exprReturnType = a.expr.getReturnType();

		// make sure that the variable is being assigned properly
		// (int -> int, bool -> bool)
		if (this.vars.get(var) == ReturnType.BOOL && exprReturnType != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch at [" + a.getLine() + ", " + a.getCol()
					+ "]: expected BOOL = BOOL assignment but got BOOL = " + exprReturnType);
		} else if (this.vars.get(var) == ReturnType.INT && exprReturnType != ReturnType.INT) {
			semanticErrors.add("Type mismatch at line [" + a.getLine() + ", " + a.getCol()
					+ "]: expected INT = INT assignment but got INT = " + a.expr.getReturnType());
		}

		// Type check the assignment expression
		a.expr.accept(this);

		return null;
	}

	@Override
	public <T> T visitIfStatement(IfStatement ifs) {
		Expression cond = ifs.cond;

		// cond must be a boolean expression
		if (cond.getReturnType() != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at [" + ifs.getLine() + ", " + ifs.getCol()
					+ "] expected ( BOOL ) but got ( " + cond.getReturnType() + " )");
		}
		// Type check the condition
		cond.accept(this);

		// Type check all inner expressions
		for (Expression e : ifs.expressions) {
			e.accept(this);
		}
		return null;
	}

	@Override
	public <T> T visitMultiplication(Multiplication mul) {
		checkIfExprArgsValidBinary(mul);
		return null;
	}

	@Override
	public <T> T visitAddition(Addition add) {
		checkIfExprArgsValidBinary(add);
		return null;
	}

	@Override
	public <T> T visitDivision(Division div) {
		checkIfExprArgsValidBinary(div);
		return null;
	}

	@Override
	public <T> T visitModulo(Modulo mod) {
		checkIfExprArgsValidBinary(mod);
		return null;
	}

	@Override
	public <T> T visitSubtraction(Subtraction sub) {
		checkIfExprArgsValidBinary(sub);
		return null;
	}

	@Override
	public <T> T visitGreaterThan(GreaterThan gt) {
		checkIfExprArgsValidBinary(gt);
		return null;
	}

	@Override
	public <T> T visitGreaterEqualThan(GreaterEqualThan ge) {
		checkIfExprArgsValidBinary(ge);
		return null;
	}

	@Override
	public <T> T visitLessThan(LessThan lt) {
		checkIfExprArgsValidBinary(lt);
		return null;
	}

	@Override
	public <T> T visitLessEqualThan(LessEqualThan le) {
		checkIfExprArgsValidBinary(le);
		return null;
	}

	@Override
	public <T> T visitEqual(Equal eq) {
		checkIfExprArgsValidBinary(eq);
		return null;
	}

	@Override
	public <T> T visitNotEqual(NotEqual ne) {
		checkIfExprArgsValidBinary(ne);
		return null;
	}

	@Override
	public <T> T visitAnd(And and) {
		checkIfExprArgsValidBinary(and);
		return null;
	}

	@Override
	public <T> T visitOr(Or or) {
		checkIfExprArgsValidBinary(or);
		return null;
	}

	@Override
	public <T> T visitNot(Not not) {
		if (not.getReturnType() != ReturnType.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at [" + not.getLine() + ", " + not.getCol()
					+ "] : expected ( BOOL ) but got ( " + not.getReturnType() + " )");
		}

		// Type checkt the inner expression
		not.expr.accept(this);

		return null;
	}

	@Override
	public <T> T visitParenthesis(Parenthesis p) {
		return p.expr.accept(this);
	}

	@Override
	public <T> T visitVariable(Variable v) {
		return null;
	}

	@Override
	public <T> T visitBooleanLiteral(BooleanLiteral bl) {
		return null;
	}

	@Override
	public <T> T visitNumberLiteral(NumberLiteral nl) {
		return null;
	}

	@Override
	public <T> T visitCharacterLiteral(CharacterLiteral cl) {
		return null;
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
	private void checkIfExprArgsValidBinary(Expression expr) {
		boolean isValid = true;

		Expression left = ((BinaryExpression) expr).left;
		Expression right = ((BinaryExpression) expr).right;
		ExprType exprType = expr.getExprType();

		// Type check left and right expressions
		left.accept(this);
		right.accept(this);

		int line = left.getLine();
		int col = left.getCol();

		ReturnType leftReturnType = left.getReturnType();
		ReturnType rightReturnType = right.getReturnType();

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
