package model.Expression.BlockContainer;

import java.util.List;

import model.Expression.Expression;

public interface BlockContainer {
	void addExpression(Expression e);

	List<Expression> getExpressions();
}