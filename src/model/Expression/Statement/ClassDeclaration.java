package model.Expression.Statement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import model.Expression.Expression;
import model.Expression.Expression.ExprType;
import model.Expression.Expression.ReturnType;
import model.Expression.OperationVisitor.OperationVisitor;

public class ClassDeclaration extends Expression {
	public List<Expression> expressions;
	public String className;
	public String superClass;

	public ReturnType getReturnType() {
		return ReturnType.NONE;
	}

	public ExprType getExprType() {
		return ExprType.NONE;
	}

	public ClassDeclaration(String className, int line, int col) {
		this.className = className;
		this.expressions = new ArrayList<>();
		this.line = line;
		this.col = col;
	}

	public Set<String> getVariables() {
		Set<String> res = new HashSet<>();
		for (Expression e : expressions) {
			res.addAll(e.getVariables());
		}
		return res;
	}

	public ClassDeclaration(String className, String superClass, int line, int col) {
		this(className, line, col);
		this.superClass = superClass;
	}

	@Override
	public int getLine() {
		// TODO Auto-generated method stub
		return line;
	}

	@Override
	public int getCol() {
		// shift left by 6 to account for the word 'class' + <space>
		return col - 6;
	}

	public void addExpression(Expression e) {
		this.expressions.add(e);
	}

	@Override
	public String toString() {
		String res = "Class " + className + (this.superClass == null ? "" : " extends " + superClass) + "{\n";
		for (Expression e : expressions) {
			res += "    " + e + "\n";
		}
		res += "}\n";
		return res;

	}

	@Override
	public <T> T accept(OperationVisitor T) {
		return T.visitClassDeclaration(this);
	}
}
