package model.Expression;

import java.util.Set;

import model.Expression.OperationVisitor.OperationVisitor;

public class CharacterLiteral extends Expression {
	public char val;

	public CharacterLiteral(char val, int line, int col) {
		this.val = val;
		this.line = line;
		this.col = col;
	}

	@Override
	public Set<String> getVariables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T accept(OperationVisitor T) {
		// TODO Auto-generated method stub
		return T.visitCharacterLiteral(this);
	}

	@Override
	public int getLine() {
		return this.line;
	}

	@Override
	public int getCol() {
		return this.col;
	}

	@Override
	public ReturnType getReturnType() {
		return ReturnType.CHAR;
	}

	@Override
	public ExprType getExprType() {
		return ExprType.NONE;
	}

	@Override
	public String toString() {
		return "" + val;
	}

}
