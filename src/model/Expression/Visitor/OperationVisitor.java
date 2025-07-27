package model.Expression.Visitor;

import java.util.Map;

import model.Assignment.ListAssignment;
import model.Assignment.PrimitiveAssignment;
import model.Expression.*;
import model.Expression.Expression.Type;
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
import model.Expression.Declaration.ListDeclaration;
import model.Expression.Declaration.PrimitaveDeclaration;
import model.Expression.Statement.*;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;

public interface OperationVisitor {

	public void updateVarState(Map<String, Type> newVars);

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
}
