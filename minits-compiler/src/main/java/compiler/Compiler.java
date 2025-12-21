package compiler;

import antlr.ExprParser;
import app.Utils;
import model.Expression.BlockContainer.BlockContainer;
import model.Expression.BlockContainer.ForLoop;
import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.ExpressionProcessor;
import model.Expression.FunctionDeclaration;
import model.Expression.Visitor.OperationVisitor;
import model.Expression.Visitor.TypeChecker;
import model.Expression.Visitor.VariableBindingChecker;
import model.Program.AntlrToProgram;
import model.Program.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Compiler {
    private  Map<String, Expression.Type> vars;
    private  Map<String, FunctionDeclaration> functions;
    private  List<String> semanticErrors;
    private  List<OperationVisitor> operationVisitors;

    public Compiler() {
    }

    public void init () {
        this.vars = new HashMap<>();
        this.functions = new HashMap<>();
        this.semanticErrors = new ArrayList<>();
        this.operationVisitors = new ArrayList<>();
    }


    /**
     *  Returns classes: a list of a compiled ClassDeclaration objects.
     *
     *  For each cd in classes:
     *  cd.functions returns a list of all functions declared in the class.
     *  cd.vars returns a map of all variables in the class alongside their values
     */
    protected List<ClassDeclaration> compile(ExprParser.ProgContext progTree) {
        init();
        // Create a list of class declarations
        List<ClassDeclaration> classes = new ArrayList<>();

        AntlrToProgram progVisitor = new AntlrToProgram(semanticErrors, vars);
        Program prog = progVisitor.visit(progTree);

        TypeChecker typeCheckerVisitor =
                new TypeChecker(semanticErrors, vars, functions, classes);
        VariableBindingChecker varDeclarationCheckerVisitor =
                new VariableBindingChecker(semanticErrors, vars, functions, classes);

        operationVisitors.add(varDeclarationCheckerVisitor);
        operationVisitors.add(typeCheckerVisitor);

        for (Expression classExpr : prog.expressions) {
            ClassDeclaration cd = (ClassDeclaration) classExpr;

            functions = new HashMap<>();
            vars = new HashMap<>();

            // visit the class declaration
            visitExpression(cd, vars, functions, classes);

            // visit expressions in the class body
            for (Expression e : cd.expressions) {
                visitExpression(e, vars, functions, classes); // vars is the top-level global map
            }

            cd.functions = functions;
            cd.vars = vars;
            cd.semanticErrors.addAll(semanticErrors);
            semanticErrors.clear();
            classes.add(cd);
        }

        ExpressionProcessor ep = new ExpressionProcessor(functions, classes);
        for (ClassDeclaration cd : classes) {
            if (cd.semanticErrors.isEmpty()) {
                ep.vars = new HashMap<>();

                // evaluate the class declaration too
                ep.evaluateExpression(cd);

                for (Expression e : cd.expressions) {
                    ep.evaluateExpression(e);
                }
                cd.setEvaluatedVars(ep.vars);
            }
        }
        return classes;
    }


    private void visitExpression(Expression e, Map<String, Expression.Type> currentVars,
                                        Map<String, FunctionDeclaration> functions, List<ClassDeclaration> classes) {

        if (!(e instanceof ForLoop)) {
            for (OperationVisitor v : operationVisitors) {
                v.updateVarState(currentVars);
                v.updateFunctionState(functions);
                e.accept(v);
            }
        }

        if (e instanceof BlockContainer) {
            Map<String, Expression.Type> savedVars = Utils.copyVarScope(currentVars);
            if (e instanceof ForLoop) {
                for (OperationVisitor v : operationVisitors) {
                    v.updateVarState(currentVars);
                    v.updateFunctionState(functions);
                    e.accept(v);
                }
            }
            for (Expression ex : ((BlockContainer) e).getExpressions()) {
                visitExpression(ex, currentVars, functions, classes);
            }

            Utils.restoreVarScope(currentVars, savedVars);
        } else if (e instanceof FunctionDeclaration) {
            /*
             * treat this as a regular expression, not a block container. only difference is
             * that u dont visit the inner statements here. instead, do it in the visitors.
             */
            FunctionDeclaration fd = ((FunctionDeclaration) e);
            if (functions.containsKey(fd.functionName)) {
                semanticErrors.add("Error at [" + fd.getLine() + ", " + fd.getCol() + "]: function " + fd.functionName
                        + " already declared");
            } else {
                functions.put(fd.functionName, fd);
            }

        }
    }

    public Map<String, Expression.Type> getVars() {
        return vars;
    }

    public Map<String, FunctionDeclaration> getFunctions() {
        return functions;
    }

    public List<String> getSemanticErrors() {
        return semanticErrors;
    }

    public List<OperationVisitor> getOperationVisitors() {
        return operationVisitors;
    }

}
