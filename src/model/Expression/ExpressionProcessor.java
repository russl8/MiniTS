package model.Expression;

import java.util.*;

import model.Expression.Expression.Type;
import model.Value;
import model.Assignment.ListAssignment;
import model.Assignment.PrimitiveAssignment;
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
import model.Expression.BlockContainer.ForLoop;
import model.Expression.BlockContainer.IfStatement;
import model.Expression.BlockContainer.WhileLoop;
import model.Expression.Declaration.ListDeclaration;
import model.Expression.Declaration.PrimitaveDeclaration;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;
import model.Expression.Util.Parameter;
import model.Expression.Visitor.Utils;

public class ExpressionProcessor {

	/**
	 * Assume
	 * 
	 * 1. all expressions are type-checked
	 * 
	 * 2. no redeclaration of variables
	 */
	public List<String> printStatements;
	public Map<String, Value> vars; // Stores values for primitive variables only
	public Map<String, FunctionDeclaration> functions;
	public List<ClassDeclaration> classes;

	public ExpressionProcessor(Map<String, FunctionDeclaration> functions, List<ClassDeclaration> classes) {
		this.printStatements = new ArrayList<>();
		this.vars = new HashMap<>();
		this.functions = functions;
		this.classes = classes;
	}

	public void evaluateExpression(Expression e) {
		if (e instanceof ClassDeclaration) {
			ClassDeclaration cd = (ClassDeclaration) e;
			// if extending from superclass, deepcopy all superclass vars
			if (cd.superClass != null) {
				ClassDeclaration superClass = Utils.getClassByClassName(classes, cd.superClass);
				Utils.deepCopyEvaluatedVars(superClass.evaluatedVars, vars);
			}
		} else if (e instanceof PrimitaveDeclaration) {
			PrimitaveDeclaration d = (PrimitaveDeclaration) e;
			Expression expr = d.initialization;
			Value val;

			if (!d.isInitialized) {
				val = new Value(d.getReturnType());
			} else if (d.isInitialized) {
				if (expr instanceof FunctionInvocation) {
					val = evaluateFunctionInvocation((FunctionInvocation) expr);
				} else {
					val = evaluateExpression(d.getReturnType(), expr);
				}
			} else {
				throw new IllegalArgumentException("Unsupported expression type: " + d.getReturnType());
			}

			// update the values map
			this.vars.put(d.var, val);

		} else if (e instanceof PrimitiveAssignment) {
			PrimitiveAssignment a = (PrimitiveAssignment) e;
			Expression expr = a.expr;
			Value val = null;
			if (expr instanceof FunctionInvocation) {
				val = evaluateFunctionInvocation((FunctionInvocation) expr);
			} else {
				val = evaluateExpression(expr.getReturnType(), expr);
			}
			this.vars.put(a.var, val);
		} else if (e instanceof ListAssignment) {
			ListAssignment ld = (ListAssignment) e;
			Value existingValue = this.vars.get(ld.var);
			existingValue.setValue(ld.expr);

		} else if (e instanceof IfStatement) {
			// evaluate expression. if true then evaluate all of its expressions
			IfStatement ifs = (IfStatement) e;
			Expression condition = ifs.cond;
			boolean conditionEvaluation = evaluateBoolean(condition);

			if (conditionEvaluation == true) {
				for (Expression ifsExpression : ifs.expressions) {
					evaluateExpression(ifsExpression);
				}
			}
		} else if (e instanceof WhileLoop) {
			WhileLoop wl = (WhileLoop) e;
			Expression condition = wl.cond;
			boolean conditionEvaluation = evaluateBoolean(condition);
			while (conditionEvaluation == true) {
				// have to reset state (scope)
				for (Expression loopExpression : wl.expressions) {
					evaluateExpression(loopExpression);

				}
				// important: evaluate loop condition after every iteration
				conditionEvaluation = evaluateBoolean(condition);
			}
		} else if (e instanceof ForLoop) {
			ForLoop fl = (ForLoop) e;
			Expression initialization = fl.initialization;
			Expression condition = fl.condition;
			Expression update = fl.update;

			this.evaluateExpression(initialization);

			boolean conditionEvaluation = evaluateBoolean(condition);
			while (conditionEvaluation == true) {
				for (Expression loopExpression : fl.expressions) {
					evaluateExpression(loopExpression);
				}
				// important: evaluate loop condition after every iteration
				this.evaluateExpression(update);
				conditionEvaluation = evaluateBoolean(condition);
			}
		} else if (e instanceof ListDeclaration) {
			ListDeclaration ld = (ListDeclaration) e;
			Value val;
			if (ld.initialization instanceof FunctionInvocation) {
				val = evaluateFunctionInvocation((FunctionInvocation) ld.initialization);

			} else {
				val = new Value(ld.type, ld.initialization);
			}
			this.vars.put(ld.var, val);

		} else if (e instanceof Parenthesis) {
			evaluateExpression(((Parenthesis) e));
		} else if (e instanceof FunctionDeclaration) {
			// do nothing
		} else {
			// not a declaration/assignment/if. ignore for now
			System.err.println("Warning, unhandled statement, ignoring for now: " + e);
		}
	}

	private Value evaluateExpression(Type type, Expression expr) {
		/**
		 * Helper to evaluate an expression given a type.
		 * 
		 * Ex: getValue(bool, expr=[true && (p && q)]) -> Value[{type=bool, value=true}]
		 */
		Value val;
		if (type == Type.BOOL) {
			val = new Value(Type.BOOL, evaluateBoolean(expr));
		} else if (type == Type.INT) {
			val = new Value(Type.INT, evaluateInteger(expr));
		} else if (type == Type.CHAR) {
			val = new Value(Type.CHAR, evaluateCharacter(expr));
		} else if (expr instanceof ListLiteral) {
			val = new Value(type, (ListLiteral) expr);
		} else if (expr instanceof Variable) {
			Variable v = (Variable) expr;
			val = this.vars.get(v.var);
		} else {
			throw new IllegalArgumentException("Unsupported expression type: " + type);
		}
		return val;
	}

	private Value evaluateFunctionInvocation(FunctionInvocation fi) {
		Map<String, Value> oldVars = new HashMap<>(this.vars);
		Map<String, Value> currentVars = this.vars;

		FunctionDeclaration fd = this.functions.get(fi.functionName);
		List<Parameter> parameters = fd.parameters;
		List<String> varsDeclaredInFunction = new ArrayList<>();

		// For each argument in function, update global state (for overriding).
		for (int i = 0; i < fi.arguments.size(); i++) {
			currentVars.put(parameters.get(i).name,
					evaluateExpression(fi.arguments.get(i).getReturnType(), fi.arguments.get(i)));
		}

		// Processs each line in function body
		for (Expression e : fd.expressions) {
			evaluateExpression(e);
			if (e instanceof ListDeclaration) {
				varsDeclaredInFunction.add(((ListDeclaration) e).var);

			}
			if (e instanceof PrimitaveDeclaration) {
				varsDeclaredInFunction.add(((PrimitaveDeclaration) e).var);
			}
		}

		// Get return value by evaluating it
		Value returnValue = this.evaluateExpression(fd.returnType, fd.returnStatement);

		// Reset state
		HashMap<String, Value> varsAfterFunction = new HashMap<>();

		for (String var : currentVars.keySet()) {
			boolean varIsParameterOfFunction = Utils.getParameter(var, parameters) != null;
			if (varsDeclaredInFunction.contains(var) || varIsParameterOfFunction && oldVars.containsKey(var)) {
				varsAfterFunction.put(var, oldVars.get(var));
			} else if (!varIsParameterOfFunction && currentVars.containsKey(var) && oldVars.containsKey(var)) {
				varsAfterFunction.put(var, currentVars.get(var));
			}
			// if is param, take value of old vars
			// if not param, take value of new vars
			// if it is a var declared in function, dont add
			// if var is redeclared in function, take value of old var
		}

		this.vars = varsAfterFunction;
		return returnValue;
	}

	private int evaluateInteger(Expression e) {

		if (e instanceof Addition) {
			Addition a = (Addition) e;
			return evaluateInteger(a.left) + evaluateInteger(a.right);
		} else if (e instanceof Subtraction) {
			Subtraction s = (Subtraction) e;
			return evaluateInteger(s.left) - evaluateInteger(s.right);
		} else if (e instanceof Multiplication) {
			Multiplication m = (Multiplication) e;
			return evaluateInteger(m.left) * evaluateInteger(m.right);
		} else if (e instanceof Division) {
			Division d = (Division) e;
			if (evaluateInteger(d.right) == 0)
				throw new Error("Error: cannot have a 0 denominator: " + d);
			return evaluateInteger(d.left) / evaluateInteger(d.right);
		} else if (e instanceof Modulo) {
			Modulo m = (Modulo) e;
			return evaluateInteger(m.left) % evaluateInteger(m.right);
		} else if (e instanceof Variable) {
			return this.vars.get(((Variable) e).var).getValueAsInt();
		} else if (e instanceof NumberLiteral) {
			return ((NumberLiteral) e).val;
		} else if (e instanceof Parenthesis) {
			return evaluateInteger(((Parenthesis) e).expr);
		} else if (e instanceof FunctionInvocation) {
			return evaluateFunctionInvocation((FunctionInvocation) e).getValueAsInt();
		}
		System.err.println("Could not properly evaluate integer expression " + e);
		return -1;
	}

	private char evaluateCharacter(Expression e) {
		if (e instanceof CharacterLiteral) {
			return ((CharacterLiteral) e).val;
		} else if (e instanceof Variable) {
			// THIS IS THE MISSING PIECE - Add variable lookup for characters
			return this.vars.get(((Variable) e).var).getValueAsCharacter();
		} else if (e instanceof Parenthesis) {
			return evaluateCharacter(((Parenthesis) e).expr);
		} else if (e instanceof FunctionInvocation) {
			return evaluateFunctionInvocation((FunctionInvocation) e).getValueAsCharacter();
		} else {
			System.err.println("Could not properly evaluate character expression " + e);
			return '\0';
		}

	}

	private boolean evaluateBoolean(Expression e) {
		if (e instanceof Not) {
			return !evaluateBoolean(((Not) e).expr);
		} else if (e instanceof Or) {
			Or o = (Or) e;
			return evaluateBoolean(o.left) || evaluateBoolean(o.right);
		} else if (e instanceof And) {
			And a = (And) e;
			return evaluateBoolean(a.left) && evaluateBoolean(a.right);
		} else if (e instanceof Parenthesis) {
			return evaluateBoolean(((Parenthesis) e).expr);
		} else if (e instanceof Equal) {
			Equal eq = (Equal) e;
			if (eq.left.getReturnType() == Type.INT) {
				return evaluateInteger(eq.left) == evaluateInteger(eq.right);
			} else if (eq.left.getReturnType() == Type.BOOL) {
				return evaluateBoolean(eq.left) == evaluateBoolean(eq.right);
			}
		} else if (e instanceof NotEqual) {
			NotEqual neq = (NotEqual) e;
			if (neq.left.getReturnType() == Type.INT) {
				return evaluateInteger(neq.left) == evaluateInteger(neq.right);
			} else if (neq.left.getReturnType() == Type.BOOL) {
				return evaluateBoolean(neq.left) == evaluateBoolean(neq.right);
			}
		} else if (e instanceof GreaterThan) {
			GreaterThan gt = (GreaterThan) e;
			return evaluateInteger(gt.left) > evaluateInteger(gt.right);
		} else if (e instanceof GreaterEqualThan) {
			GreaterEqualThan gte = (GreaterEqualThan) e;
			return evaluateInteger(gte.left) >= evaluateInteger(gte.right);
		} else if (e instanceof LessThan) {
			LessThan lt = (LessThan) e;
			return evaluateInteger(lt.left) < evaluateInteger(lt.right);
		} else if (e instanceof LessEqualThan) {
			LessEqualThan lte = (LessEqualThan) e;
			return evaluateInteger(lte.left) <= evaluateInteger(lte.right);
		} else if (e instanceof Variable) {
			return this.vars.get(((Variable) e).var).getValueAsBool();
		} else if (e instanceof BooleanLiteral) {
			return ((BooleanLiteral) e).val;
		} else if (e instanceof Parenthesis) {
			return evaluateBoolean(((Parenthesis) e).expr);
		} else if (e instanceof FunctionInvocation) {
			return evaluateFunctionInvocation((FunctionInvocation) e).getValueAsBool();
		}
		System.err.println("Could not properly evaluate boolean expression " + e + " " + e.getClass());
		return false;
	}

}
