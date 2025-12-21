package app;

import compiler.HtmlReportGenerator;
import compiler.StringCompiler;
import compiler.TextFileCompiler;
import model.Expression.ClassDeclaration;

import java.io.*;
import java.util.*;

/**
 * Main class that parses, type-checks, and evaluates one or more input files,
 * then generates styled HTML reports.
 */
public class ExpressionApp {


    public static void main(String[] args) {
//        if (args.length != 1) {
//            System.err.println("Usage: java ExpressionApp <file.txt>");
//            return;
//        }

//        File file = new File(args[0]);
//        if (!file.exists() || !file.isFile()) {
//            System.err.println("Error: file does not exist");
//            return;
//        }

//        compileSingleFile(file);
        compileSingleFileAsString("class A { a : int = 4 ; wda awdq  }");
    }

    private static void compileSingleFileAsString(String fileAsString) {

        try {

            StringCompiler compiler = new StringCompiler();
            List<ClassDeclaration> compiledClasses = compiler.compileString(fileAsString);

//            String reportPath =
//                    HtmlReportGenerator.generate(file, file.getAbsolutePath(), compiler.getSemanticErrors(), compiledClasses);

            System.out.println(compiler.getSemanticErrors());
            for (ClassDeclaration cd : compiledClasses) {
                System.out.println(cd.vars);
                System.out.println(cd.evaluatedVars);
            }

//            System.out.println("Generated report at " + reportPath);

        } catch (Exception e) {
            System.err.println("Error processing string " + fileAsString + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void compileSingleFile(File file) {

        try {

            TextFileCompiler compiler = new TextFileCompiler();
            List<ClassDeclaration> compiledClasses = compiler.compileFile(file);

            String reportPath =
                    HtmlReportGenerator.generate(file, file.getAbsolutePath(), compiler.getSemanticErrors(), compiledClasses);

            System.out.println("Generated report at " + reportPath);

        } catch (Exception e) {
            System.err.println("Error processing file " + file.getAbsolutePath() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }


}