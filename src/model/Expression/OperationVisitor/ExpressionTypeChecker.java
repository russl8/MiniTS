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
import model.Expression.Expression;
import model.Expression.NumberLiteral;
import model.Expression.Utils;
import model.Expression.Variable;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitDeclarationWithOptionalAssignment(Declaration d) {
		ReturnType varType = vars.get(d.var);
		// If declaration is initialized, typecheck its expressoin
		if (d.isInitialized) {
			// todo: add line, col as a property of variables
			// copy and paste typechecking logic
			ReturnType exprType = d.expr.getReturnType();

			if (varType != exprType) {
				semanticErrors.add("Type mismatch at [" + d.getLine() + ", " + d.getCol() + "]: expected " + varType
						+ " = " + varType + " assignment but got " + varType + " = " + exprType);
			}
		}
		return null;
	}

	@Override
	public <T> T visitAssignment(Assignment a) {
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
		return null;
	}

	@Override
	public <T> T visitMultiplication(Multiplication mul) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitAddition(Addition add) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitDivision(Division div) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitModulo(Modulo mod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitSubtraction(Subtraction sub) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitGreaterThan(GreaterThan gt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitGreaterEqualThan(GreaterEqualThan ge) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitLessThan(LessThan lt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitLessEqualThan(LessEqualThan le) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitEqual(Equal eq) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitNotEqual(NotEqual ne) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitAnd(And and) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitOr(Or or) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitNot(Not not) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitParenthesis(Parenthesis p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitVariable(Variable v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitBooleanLiteral(BooleanLiteral bl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitNumberLiteral(NumberLiteral nl) {
		// TODO Auto-generated method stub
		return null;
	}

}
