package com.russl8.mints.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import compiler.StringCompiler;
import dto.ClassResult;
import dto.CompileResult;
import model.Expression.ClassDeclaration;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompileController {

    @PostMapping("/compile")
    public Map<String, Object> compile(@RequestBody Map<String, String> body) {
        String code = body.get("code");

        if (code == null || code.isBlank()) {
            return Map.of(
                    "success", false,
                    "output", "No code provided"
            );
        }

        StringCompiler compiler = new StringCompiler();
        var classes = compiler.compileString(code);

        if (classes == null) {
            return Map.of(
                    "success", false,
                    "output", String.join("\n", compiler.getSemanticErrors())
            );
        }

        StringBuilder out = new StringBuilder();

        for (var cd : classes) {
            out.append("class ").append(cd.className).append("\n");

            if (!cd.semanticErrors.isEmpty()) {
                out.append("Errors:\n");
                for (String err : cd.semanticErrors) {
                    out.append("  ").append(err).append("\n");
                }
            } else {
                out.append("Evaluated Vars:\n");
                cd.evaluatedVars.forEach((k, v) ->
                        out.append("  ").append(k).append(" = ").append(v).append("\n")
                );
            }
            out.append("\n");
        }

        return Map.of(
                "success", true,
                "output", out.toString()
        );
    }
}
