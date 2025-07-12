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
import model.Expression.Expression;
import model.Expression.Utils;
import model.Expression.Variable;
import model.Expression.Arithmetic.Addition;
import model.Expression.Arithmetic.Division;
import model.Expression.Arithmetic.Modulo;
import model.Expression.Arithmetic.Multiplication;
import model.Expression.Arithmetic.NumberLiteral;
import model.Expression.Arithmetic.Subtraction;
import model.Expression.Equality.Equal;
import model.Expression.Equality.NotEqual;
import model.Expression.Logical.And;
import model.Expression.Logical.BooleanLiteral;
import model.Expression.Logical.Not;
import model.Expression.Logical.Or;
import model.Expression.Relational.GreaterEqualThan;
import model.Expression.Relational.GreaterThan;
import model.Expression.Relational.LessEqualThan;
import model.Expression.Relational.LessThan;
import model.Expression.Statement.Assignment;
import model.Expression.Statement.ClassDeclaration;
import model.Expression.Statement.Declaration;
import model.Expression.Statement.IfStatement;
import model.Expression.Expression.ReturnType;
import model.Expression.Parenthesis;
import model.Program.Program;

public class ExpressionTypeChecker implements OperationVisitor {

	public List<String> semanticErrors;
	public Map<String, ReturnType> vars; // stores all the variables declared in the program so far

	public ExpressionTypeChecker(List<String> semanticErrors) {
		this.vars = new HashMap<>();
		this.semanticErrors = semanticErrors;
	}

	@Override
	public <T> T visitClassDeclaration(ClassDeclaration cd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitDeclarationWithOptionalAssignment(Declaration d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitAssignment(Assignment a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitIfStatement(IfStatement ifs) {
		// TODO Auto-generated method stub
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
