package com.russl8.mints.controllers;

import compiler.StringCompiler;
import dto.ClassResult;
import dto.CompileResult;
import model.Expression.ClassDeclaration;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompileController {

    @PostMapping("/compile")
    public CompileResult compile(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        if (code == null || code.isBlank()) {
            return new CompileResult(false, List.of("No code provided"), List.of());
        }

        StringCompiler compiler = new StringCompiler();
        List<ClassDeclaration> classes = compiler.compileString(code);

        if (classes == null) {
            // if you still return null on syntax errors, you can decide how to handle:
            // ideally StringCompiler throws SyntaxException instead (400 handler)
            return new CompileResult(false, compiler.getSemanticErrors(), List.of());
        }

        List<String> semanticErrors = new ArrayList<>();
        List<ClassResult> classResults = new ArrayList<>();

        for (ClassDeclaration cd : classes) {
            if (cd.semanticErrors != null && !cd.semanticErrors.isEmpty()) {
                semanticErrors.addAll(cd.semanticErrors);
            }

            classResults.add(new ClassResult(cd.className, cd.evaluatedVars));
        }

        boolean success = semanticErrors.isEmpty();
        return new CompileResult(success, semanticErrors, classResults);
    }
}
