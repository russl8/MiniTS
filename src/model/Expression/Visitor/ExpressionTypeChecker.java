package model.Expression.Visitor;

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
import model.Assignment.Assignment;
import model.Assignment.ListAssignment;
import model.Assignment.PrimitiveAssignment;
import model.Expression.BooleanLiteral;
import model.Expression.CharacterLiteral;
import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.ListLiteral;
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
import model.Expression.Declaration.Declaration;
import model.Expression.Declaration.ListDeclaration;
import model.Expression.Declaration.PrimitaveDeclaration;
import model.Expression.Unary.Not;
import model.Expression.Unary.Parenthesis;
import model.Expression.Util.Utils;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.Type;
import model.Program.Program;

public class ExpressionTypeChecker implements OperationVisitor {

	public List<String> semanticErrors;
	public Map<String, Type> vars; // stores all the variables declared in the program so far

	public ExpressionTypeChecker(List<String> semanticErrors, Map<String, Type> vars) {
		this.vars = vars;
		this.semanticErrors = semanticErrors;
	}

	@Override
	public <T> T visitClassDeclaration(ClassDeclaration cd) {
		return null;
	}
	
	@Override
	public <T> T visitFunctionDeclaration(FunctionDeclaration fd) {
		// dont have to do anything here, invalid param decl types will be caught by parser
		return null;
	}

	@Override
	public <T> T visitPrimitaveDeclaration(PrimitaveDeclaration d) {
		Type varType = d.type;
		// If declaration is initialized, typecheck its expressoin
		if (d.isInitialized) {
			Type exprType = d.initialization.getReturnType();
			if (varType != exprType) {
				semanticErrors.add("Type mismatch at [" + d.getLine() + ", " + d.getCol() + "]: expected " + varType
						+ " = " + varType + " declaration but got " + varType + " = " + exprType);
			}
			// recursively typecheck the inner expression
			d.initialization.accept(this);
		}

		return null;
	}

	@Override
	public <T> T visitListDeclaration(ListDeclaration ld) {
		Type listType = ld.type;
		Type itemType;
		switch (listType) {
		case LIST_INT:
			itemType = Type.INT;
			break;
		case LIST_BOOL:
			itemType = Type.BOOL;
			break;
		case LIST_CHAR:
			itemType = Type.CHAR;
			break;
		default:
			itemType = Type.NONE;
			System.out.println("Unexpected type: " + listType);
		}

		if (ld.isInitialized) {
			// make sure the RHS of declaration is a list
			if (!(ld.initialization instanceof ListLiteral)) {
				semanticErrors.add(
						"Error with declaration at [" + ld.initialization.getLine() + ", " + ld.initialization.getCol()
								+ "], " + ld.initialization.getReturnType() + " cannot be assigned to a list");
				return null;
			}
			// if RHS is a list, typecheck its items
			ListLiteral ll = (ListLiteral) ld.initialization;
			for (Expression e : ll.items) {
				if (e.getReturnType() != itemType) {
					semanticErrors.add("Type mismatch in list declaration at [" + e.getLine() + ", " + e.getCol() + "] "
							+ e.getReturnType() + " found in list[" + itemType + "]");
					break;
				}

			}
		}

		return null;
	}

	@Override
	public <T> T visitPrimitiveAssignment(PrimitiveAssignment a) {
		// TODO: Recursively visit the assignment left and right.
		// do the same for visitBinaryExpression (ex: recursively visit left and right)
		String var = a.var;
		Type exprReturnType = this.vars.get(var);

		// make sure that the variable is being assigned properly
		// ex: (int -> int, bool -> bool)
		if (this.vars.get(var) != exprReturnType) {
			semanticErrors.add("Type mismatch at [" + a.getLine() + ", " + a.getCol() + "]: expected "
					+ this.vars.get(var) + " = " + this.vars.get(var) + " assignment but got " + this.vars.get(var)
					+ " = " + exprReturnType);
		}

		// Type check the assignment expression
		a.expr.accept(this);

		return null;
	}

	@Override
	public <T> T visitListAssignment(ListAssignment la) {
		Type listType = vars.get(la.var);
		Type itemType;
		switch (listType) {
		case LIST_INT:
			itemType = Type.INT;
			break;
		case LIST_BOOL:
			itemType = Type.BOOL;
			break;
		case LIST_CHAR:
			itemType = Type.CHAR;
			break;
		default:
			itemType = Type.NONE;
			System.out.println("Unexpected type: " + listType);
		}

		if (!(la.expr instanceof ListLiteral)) {
			semanticErrors.add("Error at [" + la.getLine() + ", " + la.getCol() + "]: Assigning "
					+ la.expr.getExprType() + " to list. ");
		} else {
			ListLiteral ll = (ListLiteral) la.expr;
			for (Expression e : ll.items) {
				if (e.getReturnType() != itemType) {
					semanticErrors.add("Error in [" + e.getLine() + ", " + e.getCol() + "] Cannot assign list["
							+ e.getReturnType() + "] to list[" + itemType + "]");
					break;
				}
			}

		}

		return null;
	}

	@Override
	public <T> T visitIfStatement(IfStatement ifs) {
		Expression cond = ifs.cond;

		// cond must be a boolean expression
		if (cond.getReturnType() != Type.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at [" + ifs.getLine() + ", " + ifs.getCol()
					+ "], if-statement condition must be boolean but got " + cond.getReturnType());
		}
		// Type check the condition
		cond.accept(this);

		// DO NOT type check all inner expressions. already handled in ExpressionApp.
		// for (Expression e : ifs.expressions) {
		// e.accept(this);
		// }
		return null;
	}

	@Override
	public <T> T visitWhileLoop(WhileLoop wl) {
		/*
		 * make sure the whileloop condition has a returnType of BOOL typecheck the
		 * condition typecheck all inner expressions
		 */
		Expression cond = wl.cond;

		// cond must be a boolean expression
		if (cond.getReturnType() != Type.BOOL) {
			semanticErrors.add("Type mismatch in logical expression at [" + wl.getLine() + ", " + wl.getCol()
					+ "], while-loop condition must be boolean but got " + cond.getReturnType());
		}
		// Type check the condition
		cond.accept(this);

		// DO NOT type check all inner expressions. already handled in ExpressionApp.
		// for (Expression e : ifs.expressions) {
		// e.accept(this);
		// }
		return null;
	}

	@Override
	public <T> T visitForLoop(ForLoop fl) {
		Expression initialization, condition, update;
		initialization = fl.initialization;
		condition = fl.condition;
		update = fl.update;

		if (!(initialization instanceof Declaration)) {
			// initialization must be a declaration
			semanticErrors.add("Error in forloop expression at [" + initialization.getLine() + ", "
					+ initialization.getCol() + "], for-loop first expression must be a declaration, instead got "
					+ initialization.getClass());
		}
		if (condition.getReturnType() != Type.BOOL) {
			// condition must be a boolean expression
			semanticErrors.add("Error in forloop expression at [" + condition.getLine() + ", " + condition.getCol()
					+ "], for-loop condition must be a boolean expression, instead got " + condition.getReturnType());
		}

		// update must be an assignmnet
		if (!(update instanceof Assignment)) {
			semanticErrors.add("Error in forloop expression at [" + update.getLine() + ", " + update.getCol()
					+ "], for-loop last expression must be an assignment, instead got " + update.getClass());
		}

		initialization.accept(this);
		condition.accept(this);
		update.accept(this);

		return null;
	}

	@Override
	public <T> T visitBinaryExpression(BinaryExpression be) {
		checkIfExprArgsValidBinary(be);
		return null;
	}

	@Override
	public <T> T visitNot(Not not) {
		if (not.getReturnType() != Type.BOOL) {
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

	@Override
	public void updateVarState(Map<String, Type> newVars) {
		this.vars = newVars;
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

		Type leftReturnType = left.getReturnType() != null ? left.getReturnType() : this.vars.get(left.toString());
		Type rightReturnType = right.getReturnType() != null ? right.getReturnType() : this.vars.get(right.toString());

		String typeOfExpression = "";
		String expectedTypes = "";
		String actualTypes = "(" + leftReturnType + ", " + rightReturnType + ")";

		if (exprType == ExprType.LOGICAL) {
			typeOfExpression = "logical";
			expectedTypes = "(BOOL, BOOL)";
			isValid = leftReturnType == Type.BOOL && rightReturnType == Type.BOOL;

		} else if (exprType == ExprType.ARITHMETIC) {
			typeOfExpression = "arithmetic";
			expectedTypes = "(INT, INT)";
			isValid = leftReturnType == Type.INT && rightReturnType == Type.INT;

		} else if (exprType == ExprType.RELATIONAL) {
			typeOfExpression = "relational";
			expectedTypes = "(INT, INT)";
			isValid = leftReturnType == Type.INT && rightReturnType == Type.INT;

		} else if (exprType == ExprType.EQUALITY) {
			typeOfExpression = "equality";
			expectedTypes = "(INT, INT) or (BOOL, BOOL)";
			isValid = (leftReturnType == rightReturnType)
					&& (leftReturnType == Type.INT || leftReturnType == Type.BOOL);
		}

		if (!isValid) {
			System.out.println("error with " + expr + vars);
			semanticErrors.add("Type mismatch in " + typeOfExpression + " expression at [" + line + ", " + col
					+ "]: expected " + expectedTypes + " but got " + actualTypes + "");

		}
	}

}
