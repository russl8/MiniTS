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
	 * Enter a parse tree produced by the {@code AttributesSection}
	 * labeled alternative in {@link ExprParser#attributes_section}.
	 * @param ctx the parse tree
	 */
	void enterAttributesSection(ExprParser.AttributesSectionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttributesSection}
	 * labeled alternative in {@link ExprParser#attributes_section}.
	 * @param ctx the parse tree
	 */
	void exitAttributesSection(ExprParser.AttributesSectionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttributeDeclaration}
	 * labeled alternative in {@link ExprParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttributeDeclaration(ExprParser.AttributeDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttributeDeclaration}
	 * labeled alternative in {@link ExprParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttributeDeclaration(ExprParser.AttributeDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OperationsSection}
	 * labeled alternative in {@link ExprParser#operations_section}.
	 * @param ctx the parse tree
	 */
	void enterOperationsSection(ExprParser.OperationsSectionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OperationsSection}
	 * labeled alternative in {@link ExprParser#operations_section}.
	 * @param ctx the parse tree
	 */
	void exitOperationsSection(ExprParser.OperationsSectionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code OperationDeclaration}
	 * labeled alternative in {@link ExprParser#operation}.
	 * @param ctx the parse tree
	 */
	void enterOperationDeclaration(ExprParser.OperationDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code OperationDeclaration}
	 * labeled alternative in {@link ExprParser#operation}.
	 * @param ctx the parse tree
	 */
	void exitOperationDeclaration(ExprParser.OperationDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code DoBlock}
	 * labeled alternative in {@link ExprParser#do_block}.
	 * @param ctx the parse tree
	 */
	void enterDoBlock(ExprParser.DoBlockContext ctx);
	/**
	 * Exit a parse tree produced by the {@code DoBlock}
	 * labeled alternative in {@link ExprParser#do_block}.
	 * @param ctx the parse tree
	 */
	void exitDoBlock(ExprParser.DoBlockContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AttributeAssignment}
	 * labeled alternative in {@link ExprParser#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAttributeAssignment(ExprParser.AttributeAssignmentContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AttributeAssignment}
	 * labeled alternative in {@link ExprParser#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAttributeAssignment(ExprParser.AttributeAssignmentContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Multiplication}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterMultiplication(ExprParser.MultiplicationContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Multiplication}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitMultiplication(ExprParser.MultiplicationContext ctx);
	/**
	 * Enter a parse tree produced by the {@code Addition}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterAddition(ExprParser.AdditionContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Addition}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitAddition(ExprParser.AdditionContext ctx);
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
	 * Enter a parse tree produced by the {@code Number}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void enterNumber(ExprParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by the {@code Number}
	 * labeled alternative in {@link ExprParser#expr}.
	 * @param ctx the parse tree
	 */
	void exitNumber(ExprParser.NumberContext ctx);
}