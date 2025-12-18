package model.Expression.Visitor;

import java.util.Map;

import model.Assignment.ListAssignment;
import model.Assignment.PrimitiveAssignment;
import model.Expression.*;
import model.Expression.Expression.Type;
import model.Expression.Binary.BinaryExpression;
import model.Expression.BlockContainer.*;
import model.Expression.Declaration.ListDeclaration;
import model.Expression.Declaration.PrimitaveDeclaration;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;

public interface OperationVisitor {

	public void updateVarState(Map<String, Type> newVars);

	public void updateFunctionState(Map<String, FunctionDeclaration> functions);

	public <T> T visitFunctionInvocation(FunctionInvocation fi);

	public <T> T visitFunctionDeclaration(FunctionDeclaration fd);

	public <T> T visitClassDeclaration(ClassDeclaration cd);

	public <T> T visitPrimitaveDeclaration(PrimitaveDeclaration d);

	public <T> T visitWhileLoop(WhileLoop wl);

	public <T> T visitBinaryExpression(BinaryExpression be);

	public <T> T visitListDeclaration(ListDeclaration ld);

	public <T> T visitPrimitiveAssignment(PrimitiveAssignment a);

	public <T> T visitListAssignment(ListAssignment la);

	public <T> T visitIfStatement(IfStatement ifs);

	public <T> T visitNot(Not not);

	public <T> T visitParenthesis(Parenthesis p);

	public <T> T visitVariable(Variable v);

	public <T> T visitBooleanLiteral(BooleanLiteral bl);

	public <T> T visitNumberLiteral(NumberLiteral nl);

	public <T> T visitCharacterLiteral(CharacterLiteral cl);

	public <T> T visitForLoop(ForLoop fl);
}
