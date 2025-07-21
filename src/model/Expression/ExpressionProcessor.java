package model.Expression;

import java.util.*;

import model.Expression.Expression.ReturnType;
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
import model.Expression.Statement.Declaration;
import model.Expression.Statement.IfStatement;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;

public class ExpressionProcessor {

	/**
	 * Assume
	 * 
	 * 1. all expressions are type-checked
	 * 
	 * 2. no redeclaration of variables
	 */
	public List<String> printStatements;
	public Map<String, Value> values; // key=var

	public ExpressionProcessor() {
		this.printStatements = new ArrayList<>();
		this.values = new HashMap<>();
	}

	public void evaluateExpression(Expression e) {
		if (e instanceof Declaration) {
			Declaration d = (Declaration) e;
			Expression expr = d.expr;
			Value val;

			if (!d.isInitialized) {
				val = new Value(d.getReturnType());
			} else if (d.getReturnType() == ReturnType.BOOL) {
				val = new Value(ReturnType.BOOL, evaluateBoolean(expr));
			} else if (d.getReturnType() == ReturnType.INT) {
				val = new Value(ReturnType.INT, evaluateInteger(expr));
			} else {
				throw new IllegalArgumentException("Unsupported expression type: " + d.getReturnType());
			}

			this.values.put(d.var, val);
//			System.out.println(e + " " + this.values);
		} else if (e instanceof Assignment) {
			Assignment a = (Assignment) e;
			Value val;
//			System.out.println("Looking at assignment " + a);
			Expression expr = a.expr;
			if (expr.getReturnType() == ReturnType.BOOL) {
				val = new Value(ReturnType.BOOL, evaluateBoolean(expr));
			} else if (expr.getReturnType() == ReturnType.INT) {
				val = new Value(ReturnType.INT, evaluateInteger(expr));
			} else {
				throw new IllegalArgumentException("Unsupported expression type: " + expr.getReturnType());
			}
			this.values.put(a.var, val);
//			System.out.println(e + " " + this.values);
		} else if (e instanceof IfStatement) {
			// evaluate expression. if true then evaluate all of its expressions
			IfStatement ifs = (IfStatement) e;
//			System.out.println("Looking at ifStatement " + ifs);
			Expression condition = ifs.cond;
			boolean conditionEvaluation = evaluateBoolean(condition);

			if (conditionEvaluation == true) {
				for (Expression ifsExpression : ifs.expressions) {
					evaluateExpression(ifsExpression);
				}
			} else {

			}
		} else {
			// not a declaration/assignment/if. ignore for now
			System.err.println("Warning, unhandled statement, ignoring for now: " + e);
		}
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
			return this.values.get(((Variable) e).var).getValueAsInt();
		} else if (e instanceof NumberLiteral) {
			return ((NumberLiteral) e).val;
		}
		System.err.println("Could not properly evaluate integer expression " + e);
		return -1;
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
			return evaluateBoolean(eq.left) == evaluateBoolean(eq.right);
		} else if (e instanceof NotEqual) {
			NotEqual neq = (NotEqual) e;
			return evaluateBoolean(neq.left) != evaluateBoolean(neq.right);
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
			return this.values.get(((Variable) e).var).getValueAsBool();
		} else if (e instanceof BooleanLiteral) {
			return ((BooleanLiteral) e).val;
		}
		System.err.println("Could not properly evaluate boolean expression " + e);
		return false;
	}
}
