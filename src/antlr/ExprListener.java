// Generated from Expr.g4 by ANTLR 4.13.2

    package antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ExprParser}.
 */
public interface ExprListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by the {@code Program}
	 * labeled alternative in {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void enterProgram(ExprParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Program}
	 * labeled alternative in {@link ExprParser#prog}.
	 * @param ctx the parse tree
	 */
	void exitProgram(ExprParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ClassDeclaration}
	 * labeled alternative in {@link ExprParser#class_decl}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(ExprParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ClassDeclaration}
	 * labeled alternative in {@link ExprParser#class_decl}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(ExprParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ClassBody}
	 * labeled alternative in {@link ExprParser#class_body}.
	 * @param ctx the parse tree
	 */
	void enterClassBody(ExprParser.ClassBodyContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ClassBody}
	 * labeled alternative in {@link ExprParser#class_body}.
	 * @param ctx the parse tree
	 */
	void exitClassBody(ExprParser.ClassBodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(ExprParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(ExprParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DeclarationWithOptionalAssignment}
	 * labeled alternative in {@link ExprParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclarationWithOptionalAssignment(ExprParser.DeclarationWithOptionalAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DeclarationWithOptionalAssignment}
	 * labeled alternative in {@link ExprParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclarationWithOptionalAssignment(ExprParser.DeclarationWithOptionalAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code VariableAssignment}
	 * labeled alternative in {@link ExprParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterVariableAssignment(ExprParser.VariableAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code VariableAssignment}
	 * labeled alternative in {@link ExprParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitVariableAssignment(ExprParser.VariableAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link ExprParser#conditional}.
	 * @param ctx the parse tree
	 */
	void enterIfStatement(ExprParser.IfStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IfStatement}
	 * labeled alternative in {@link ExprParser#conditional}.
	 * @param ctx the parse tree
	 */
	void exitIfStatement(ExprParser.IfStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterVariable(ExprParser.VariableContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Variable}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitVariable(ExprParser.VariableContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BooleanLiteral}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBooleanLiteral(ExprParser.BooleanLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BooleanLiteral}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBooleanLiteral(ExprParser.BooleanLiteralContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(ExprParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(ExprParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterParenExpr(ExprParser.ParenExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ParenExpr}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitParenExpr(ExprParser.ParenExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NumberLiteral}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumberLiteral(ExprParser.NumberLiteralContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NumberLiteral}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumberLiteral(ExprParser.NumberLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link ExprParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(ExprParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ExprParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(ExprParser.TypeContext ctx);
}