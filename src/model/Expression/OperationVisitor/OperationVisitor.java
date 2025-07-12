package model.Expression.OperationVisitor;

import model.Expression.*;
import model.Expression.Arithmetic.*;
import model.Expression.Equality.*;
import model.Expression.Logical.*;
import model.Expression.Relational.*;
import model.Expression.Statement.*;

public interface OperationVisitor {
	public <T> T visitClassDeclaration(ClassDeclaration cd);

	public <T> T visitDeclarationWithOptionalAssignment(Declaration d);

	public <T> T visitAssignment(Assignment a);

	public <T> T visitIfStatement(IfStatement ifs);

	public <T> T visitMultiplication(Multiplication mul);

	public <T> T visitAddition(Addition add);

	public <T> T visitDivision(Division div);

	public <T> T visitModulo(Modulo mod);

	public <T> T visitSubtraction(Subtraction sub);

	public <T> T visitGreaterThan(GreaterThan gt);

	public <T> T visitGreaterEqualThan(GreaterEqualThan ge);

	public <T> T visitLessThan(LessThan lt);

	public <T> T visitLessEqualThan(LessEqualThan le);

	public <T> T visitEqual(Equal eq);

	public <T> T visitNotEqual(NotEqual ne);

	public <T> T visitAnd(And and);

	public <T> T visitOr(Or or);

	public <T> T visitNot(Not not);

	public <T> T visitParenthesis(Parenthesis p);

	public <T> T visitVariable(Variable v);

	public <T> T visitBooleanLiteral(BooleanLiteral bl);

	public <T> T visitNumberLiteral(NumberLiteral nl);
}
