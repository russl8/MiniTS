package model.Expression.Visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Assignment.ListAssignment;
import model.Assignment.PrimitiveAssignment;
import model.Expression.BooleanLiteral;
import model.Expression.CharacterLiteral;
import model.Expression.ClassDeclaration;
import model.Expression.NumberLiteral;
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
import model.Expression.BlockContainer.ForLoop;
import model.Expression.BlockContainer.FunctionDeclaration;
import model.Expression.BlockContainer.IfStatement;
import model.Expression.BlockContainer.WhileLoop;
import model.Expression.Declaration.ListDeclaration;
import model.Expression.Declaration.PrimitaveDeclaration;
import model.Expression.Expression.Type;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;
import model.Expression.Util.Parameter;

public class ExpressionVariableDeclarationChecker implements OperationVisitor {
	public List<String> semanticErrors;
	public Map<String, Type> vars; // stores all the variables declared in the program so far

	public ExpressionVariableDeclarationChecker(List<String> semanticErrors, Map<String, Type> vars) {
		this.vars = vars;
		this.semanticErrors = semanticErrors;
	}

	@Override
	public void updateVarState(Map<String, Type> newVars) {
		this.vars = newVars;
	}

	@Override
	public <T> T visitClassDeclaration(ClassDeclaration cd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitPrimitaveDeclaration(PrimitaveDeclaration d) {
		// TODO Auto-generated method stub
		String var = d.var;
		if (this.vars.keySet().contains(var)) {
			semanticErrors.add("Variable " + var + " already declared,  line=" + d.getLine() + " col=" + d.getCol());
		} else {
			this.vars.put(var, d.type);
		}

		if (d.initialization != null) {
			d.initialization.accept(this);
		}
		return null;
	}

	@Override
	public <T> T visitWhileLoop(WhileLoop wl) {
		wl.cond.accept(this);
		return null;
	}

	@Override
	public <T> T visitForLoop(ForLoop fl) {
		fl.initialization.accept(this);
		fl.condition.accept(this);
		fl.update.accept(this);

		return null;
	}

	@Override
	public <T> T visitListDeclaration(ListDeclaration ld) {
		// TODO Auto-generated method stub
		String var = ld.var;
		if (this.vars.keySet().contains(var)) {
			semanticErrors.add("Variable " + var + " already declared,  line=" + ld.getLine() + " col=" + ld.getCol());
		} else {
			this.vars.put(var, ld.type);
		}
		ld.initialization.accept(this);
		return null;
	}

	@Override
	public <T> T visitPrimitiveAssignment(PrimitiveAssignment a) {
		String var = a.var;
		if (!this.vars.containsKey(var)) {
			semanticErrors
					.add("Assignment to an undeclared variable in [" + a.getLine() + ", " + a.getCol() + "]: " + var);
		}
		a.expr.accept(this);
		return null;
	}

	@Override
	public <T> T visitBinaryExpression(BinaryExpression be) {
		be.left.accept(this);
		be.right.accept(this);
		return null;
	}

	@Override
	public <T> T visitListAssignment(ListAssignment a) {
		String var = a.var;
		if (!this.vars.containsKey(var)) {
			semanticErrors
					.add("Assignment to an undeclared variable in [" + a.getLine() + ", " + a.getCol() + "]: " + var);
		}
		a.expr.accept(this);
		return null;
	}

	@Override
	public <T> T visitIfStatement(IfStatement ifs) {
		// TODO Auto-generated method stub
		ifs.cond.accept(this);
		return null;
	}

	@Override
	public <T> T visitNot(Not not) {
		// TODO Auto-generated method stub
		not.expr.accept(this);
		return null;
	}

	@Override
	public <T> T visitParenthesis(Parenthesis p) {
		p.expr.accept(this);
		return null;
	}

	@Override
	public <T> T visitVariable(Variable v) {
		// check if variable is declared
		if (!this.vars.containsKey(v.var)) {
			semanticErrors.add("Variable '" + v.var + "' not declared, line=" + v.getLine() + " col=" + v.getCol());
		}
		v.setReturnType(this.vars.get(v.var));
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

	@Override
	public <T> T visitCharacterLiteral(CharacterLiteral cl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T visitFunctionDeclaration(FunctionDeclaration fd) {
		// no duplicate parameters
		List<Parameter> params = fd.parameters;
		Set<String> vars = new HashSet<>();
		for (Parameter param : params) {
			String paramName = param.name;
			if (vars.contains(paramName)) {
				semanticErrors
						.add("Variable "+paramName + " is being used multiple times in function: [" + fd.getLine() + ", " + fd.getCol() + "]");
				break;
			}
			vars.add(paramName);
		}

		return null;
	}

}
