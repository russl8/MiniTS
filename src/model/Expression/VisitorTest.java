package model.Expression;

import antlr.ExprBaseVisitor;
import antlr.ExprParser.ProgramContext;
import antlr.ExprParser.VariableContext;

public class VisitorTest extends ExprBaseVisitor<Expression> {

	@Override
	public Expression visitProgram(ProgramContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("HIHIIHIHIIH");
		return super.visitProgram(ctx);
	}

	@Override
	public Expression visitVariable(VariableContext ctx) {
		// TODO Auto-generated method stub
		System.out.println("HIHIIHIHIIH");

		return super.visitVariable(ctx);
	}

}
