package app;

import antlr.ExprLexer;
import antlr.ExprParser;
import model.*;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*
 * Note.
 * 	This class in the initial starter project is not expected to compile.
 * 	But it should compile after the skeleton code is generated into the `antlr` package.
 */

public class ExpressionApp {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.print("file name");
        } else {
            String fileName = args[0];
            ExprParser parser = getParser(fileName);
            ParseTree AST = parser.prog();

            if (MyErrorListener.hasError) {

            } else {
                AntlrToProgram progVisitor = new AntlrToProgram();
                Program prog = progVisitor.visit(AST);
                if (progVisitor.semanticErrors.isEmpty()) {
                    // Your implementation goes here.
                    processProgram(prog);
                } else {
                    for (String err : progVisitor.semanticErrors) {
                        System.out.println(err);
                    }
                }
            }
        }
    }

    private static void processProgram(Program prog) {
        for (Expression expr : prog.expressions) {
            if (expr instanceof ClassDeclaration classDecl) {
                processClass(classDecl);
            }
        }
    }

    private static void processClass(ClassDeclaration classDecl) {
        System.out.println("In " + classDecl.className + ":");
        System.out.println("\t# of attributes: " + classDecl.attributes.size());
        System.out.println("\t# of operations: " + classDecl.operations.size());
        System.out.println();

        // Create one evaluator for the entire class to maintain state between operations
        ExpressionEvaluator evaluator = new ExpressionEvaluator();

        // Initialize all attributes to 0
        for (AttributeDeclaration attr : classDecl.attributes) {
            evaluator.setVariable(attr.name, 0);
        }

        // Process each operation, maintaining state
        for (OperationDeclaration op : classDecl.operations) {
            processOperation(op, evaluator);
        }
    }

    private static void processOperation(OperationDeclaration op, ExpressionEvaluator evaluator) {
        // Track which variables are modified in this operation
        Set<String> modifiedVars = new HashSet<>();

        // Execute assignments
        for (Assignment assign : op.assignments) {
            modifiedVars.add(assign.variable);
            int value = evaluator.evaluate(assign.value);
            evaluator.setVariable(assign.variable, value);
        }

        // Output results
        System.out.println("List of attributes changed by operation `" + op.name + "`:");
        for (String varName : modifiedVars) {
            int finalValue = evaluator.getVariable(varName);
            System.out.println("\t" + varName + ": final value is " + finalValue + " ");
        }
        System.out.println(); // Add blank line after each operation
    }

    private static ExprParser getParser(String fileName) {
        ExprParser parser = null;

        try {
            CharStream input = CharStreams.fromFileName(fileName);
            ExprLexer lexer = new ExprLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            parser = new ExprParser(tokens);

            parser.removeErrorListeners();
            parser.addErrorListener(new MyErrorListener());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return parser;
    }

}