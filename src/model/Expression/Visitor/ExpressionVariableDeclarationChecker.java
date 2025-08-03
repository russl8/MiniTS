package model.Expression.Visitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.Assignment.ListAssignment;
import model.Assignment.PrimitiveAssignment;
import model.Expression.BooleanLiteral;
import model.Expression.CharacterLiteral;
import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.FunctionDeclaration;
import model.Expression.FunctionInvocation;
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
	public Map<String, FunctionDeclaration> functions;
	public List<ClassDeclaration> classes;
	private boolean isVisitingFunctionDeclaration = false;
	private Map<String, Type> functionScope;

	public ExpressionVariableDeclarationChecker(List<String> semanticErrors, Map<String, Type> vars,
			Map<String, FunctionDeclaration> functions, List<ClassDeclaration> classes) {
		this.vars = vars;
		this.semanticErrors = semanticErrors;
		this.functions = functions;
		this.classes = classes;
	}

	@Override
	public void updateVarState(Map<String, Type> newVars) {
		this.vars = newVars;
	}

	@Override
	public <T> T visitClassDeclaration(ClassDeclaration cd) {
		String superClassName = cd.superClass;

		// Exit if class doesnt have a superclass
		if (superClassName == null)
			return null;

		// make sure superclass exists in the functions map
		ClassDeclaration superClass = Utils.getClassByClassName(classes, superClassName);
		if (superClass == null) {
			semanticErrors.add(
					"Error at " + Utils.getErrorLocation(cd) + ": superclass " + superClassName + " does not exist");

		} else {
			// if superclass exists, copy all vars and functions from superclass to this
			// class
			this.vars.putAll(superClass.vars);
			this.functions.putAll(superClass.functions);
		}

		return null;
	}

	@Override
	public <T> T visitFunctionInvocation(FunctionInvocation fi) {
		String functionName = fi.functionName;
		String errorLocation = "[" + fi.getLine() + ", " + fi.getCol() + "]";
		if (!this.functions.containsKey(functionName)) {
			semanticErrors.add("Error at " + errorLocation + ": function " + functionName + " does not exist");
		} else if (fi.arguments.size() != functions.get(functionName).parameters.size()) {
			semanticErrors.add("Error at " + errorLocation + ": function " + functionName + " requires "
					+ fi.arguments.size() + " parameters. Needs " + functions.get(functionName).parameters.size());
		} else {
			fi.setReturnType(functions.get(fi.functionName).returnType);
		}

		return null;
	}

	@Override
	public void updateFunctionState(Map<String, FunctionDeclaration> functions) {
		this.functions = functions;
	}

	@Override
	public <T> T visitPrimitaveDeclaration(PrimitaveDeclaration d) {
		// TODO Auto-generated method stub
		String var = d.var;

		if (isVisitingFunctionDeclaration) {

			if (this.functionScope.keySet().contains(var)) {
				semanticErrors
						.add("Variable " + var + " already declared,  line=" + d.getLine() + " col=" + d.getCol());
			} else {
				this.functionScope.put(var, d.type);
			}

		} else {

			if (this.vars.keySet().contains(var)) {
				semanticErrors
						.add("Variable " + var + " already declared,  line=" + d.getLine() + " col=" + d.getCol());
			} else {
				this.vars.put(var, d.type);
			}

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
		String var = ld.var;
		if (isVisitingFunctionDeclaration) {
			if (this.functionScope.keySet().contains(var)) {
				semanticErrors
						.add("Variable " + var + " already declared,  line=" + ld.getLine() + " col=" + ld.getCol());
			} else {
				this.functionScope.put(var, ld.type);
			}
		} else {
			if (this.vars.keySet().contains(var)) {
				semanticErrors
						.add("Variable " + var + " already declared,  line=" + ld.getLine() + " col=" + ld.getCol());
			} else {
				this.vars.put(var, ld.type);
			}
		}

		ld.initialization.accept(this);
		return null;
	}

	@Override
	public <T> T visitPrimitiveAssignment(PrimitiveAssignment a) {
		String var = a.var;
		if (isVisitingFunctionDeclaration) {
			if (!this.vars.containsKey(var) && !this.functionScope.containsKey(var)) {
				semanticErrors.add(
						"Assignment to an undeclared variable in [" + a.getLine() + ", " + a.getCol() + "]: " + var);
			}
		} else {

			if (!this.vars.containsKey(var)) {
				semanticErrors.add(
						"Assignment to an undeclared variable in [" + a.getLine() + ", " + a.getCol() + "]: " + var);
			}
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
		if (isVisitingFunctionDeclaration) {
			if (!this.vars.containsKey(var) && !this.functionScope.containsKey(var)) {
				semanticErrors.add(
						"Assignment to an undeclared variable in [" + a.getLine() + ", " + a.getCol() + "]: " + var);
			}
		} else {
			if (!this.vars.containsKey(var)) {
				semanticErrors.add(
						"Assignment to an undeclared variable in [" + a.getLine() + ", " + a.getCol() + "]: " + var);
			}

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
		if (isVisitingFunctionDeclaration) {
			if (!this.vars.containsKey(v.var) && !this.functionScope.containsKey(v.var)) {
				semanticErrors.add("Variable '" + v.var + "' not declared, line=" + v.getLine() + " col=" + v.getCol());
			}
			v.setReturnType(
					this.functionScope.get(v.var) == null ? this.vars.get(v.var) : this.functionScope.get(v.var));
		} else {
			if (!this.vars.containsKey(v.var)) {
				semanticErrors.add("Variable '" + v.var + "' not declared, line=" + v.getLine() + " col=" + v.getCol());
			}
			v.setReturnType(this.vars.get(v.var));
		}

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
		Set<String> varSet = new HashSet<>();
		for (Parameter param : params) {
			String paramName = param.name;
			if (varSet.contains(paramName)) {
				semanticErrors.add("Variable " + paramName + " is being used multiple times in function: ["
						+ fd.getLine() + ", " + fd.getCol() + "]");
				break;
			}
			varSet.add(paramName);
		}

		if (this.vars.containsKey(fd.functionName)) {
			semanticErrors.add(
					"Variable " + fd.functionName + " is already in use: [" + fd.getLine() + ", " + fd.getCol() + "]");
		}
		this.vars.put("Vars: " + fd.functionName, fd.returnType);
		// for each param, add it to vars (remember in expressionApp, created a new
		// scope)

		this.isVisitingFunctionDeclaration = true;

		this.functionScope = new HashMap<String, Type>();

		for (Parameter param : params) {
			this.functionScope.put(param.name, param.type);
		}

		// check all function body statements
		for (Expression e : fd.expressions) {
			e.accept(this);
		}

		// check function return statement
		fd.returnStatement.accept(this);

		// cleanup
		this.functionScope = null;
		this.isVisitingFunctionDeclaration = false;

		/*
		 * create new map<String,type> functionScope set
		 * isVisitingFunctionDeclaration=true
		 * 
		 * for each expression in the function: visit them if declaration: declare in
		 * functionScope if assignment, check for var. existance in BOTH this.vars &
		 * functionScope
		 */

		return null;
	}

}
