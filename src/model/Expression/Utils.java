package model.Expression;

import model.Expression.Unary.Parenthesis;

public class Utils {
	/**
	 * Takes an expression and removes its parentheses to get its return type.
	 * 
	 * @param expr expression such as "((( 2 < 2 )))" whose returnType is NONE
	 * @return an expression without parentheses such as "2 < 2" whose returnType is BOOL
	 */
	public static Expression unwrapParentheses(Expression expr) {
		Expression cleanedExpr = expr;
		while (cleanedExpr instanceof Parenthesis) {
			cleanedExpr = ((Parenthesis) cleanedExpr).expr;
		}
		return cleanedExpr;
	}
}
