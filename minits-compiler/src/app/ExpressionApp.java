package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import dto.CompileResult;
import model.*;
import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.ExpressionProcessor;
import model.Expression.FunctionDeclaration;
import model.Expression.BlockContainer.BlockContainer;
import model.Expression.BlockContainer.ForLoop;
import model.Expression.Expression.Type;
import model.Expression.Visitor.TypeChecker;
import model.Expression.Visitor.VariableBindingChecker;
import model.Expression.Visitor.OperationVisitor;
import model.Program.AntlrToProgram;
import model.Program.Program;
import model.MyErrorListener;
import org.antlr.v4.runtime.*;

import java.io.*;
import java.util.*;

/**
 * Main class that parses, type-checks, and evaluates one or more input files,
 * then generates styled HTML reports.
 */
public class ExpressionApp {
    private static Map<String, Type> vars;
    private static Map<String, FunctionDeclaration> functions;
    private static List<String> semanticErrors;
    private static List<OperationVisitor> operationVisitors;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java ExpressionApp <file.txt>");
            return;
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.isFile()) {
            System.err.println("Error: file does not exist");
            return;
        }

        compileSingleFile(file);
    }
    private static ExprParser getParser(String fileName) {
        try {
            CharStream input = CharStreams.fromFileName(fileName);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            ExprParser parser = new ExprParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new MyErrorListener());
            return parser;
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName + " - " + e.getMessage());
            return null;
        }
    }

    private static void compileSingleFile(File file) {
        String filePath = file.getAbsolutePath();
        System.out.println("Processing file: " + filePath);

        ExprParser parser = getParser(filePath);
        if (parser == null || MyErrorListener.hasError) {
            System.err.println("Skipping file due to syntax errors or parser failure.");
            return;
        }

        try {
            semanticErrors = new ArrayList<>();
            operationVisitors = new ArrayList<>();

            // Create a list of class declarations
            List<ClassDeclaration> classes = new ArrayList<>();

            vars = new HashMap<>();
            functions = new HashMap<>();

            AntlrToProgram progVisitor = new AntlrToProgram(semanticErrors, vars);
            Program prog = progVisitor.visit(parser.prog());

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

            /**
             *  cd.functions returns a list of all functions declared in the class
             *  cd.vars returns a map of all variables in the class alongside their values
             */

            String reportPath =
                    HtmlReportGenerator.generate(file, filePath, ep, semanticErrors, classes);

            System.out.println("Generated report at " + reportPath);

        } catch (Exception e) {
            System.err.println("Error processing file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void visitExpression(Expression e, Map<String, Type> currentVars,
                                        Map<String, FunctionDeclaration> functions, List<ClassDeclaration> classes) {

        if (!(e instanceof ForLoop)) {
            for (OperationVisitor v : operationVisitors) {
                v.updateVarState(currentVars);
                v.updateFunctionState(functions);
                e.accept(v);
            }
        }

        if (e instanceof BlockContainer) {
            Map<String, Type> savedVars = Utils.copyVarScope(currentVars);
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




}