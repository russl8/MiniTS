package model.Program;

import antlr.ExprBaseVisitor;
import antlr.ExprParser;
import model.Expression.AntlrToExpression;
import model.Expression.Expression.PrimitiveType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AntlrToProgram extends ExprBaseVisitor<Program> {

	public List<String> semanticErrors; // to be accessed by the main application program
	public Map<String, PrimitiveType> vars;

	public AntlrToProgram(List<String> semanticErrors, Map<String, PrimitiveType> vars) {
		super();
		this.semanticErrors = semanticErrors;
		this.vars = vars;
	}

	@Override
	public Program visitProgram(ExprParser.ProgramContext ctx) {
		Program prog = new Program();
		// a helper visitor for transforming each subtree into an Expression object
		AntlrToExpression exprVisitor = new AntlrToExpression(semanticErrors, vars);
		for (int i = 0; i < ctx.getChildCount(); i++) {
			if (i == ctx.getChildCount() - 1) {
				// last child the start symbol prog is EOF
				// Do not visit this child and attempt to convert it to an Expression object
			} else {
				prog.addExpression(exprVisitor.visit(ctx.getChild(i)));
			}
		}

		return prog;
	}
}
