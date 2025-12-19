package controllers;

import compiler.StringCompiler;
import dto.CompileResult;
import model.Expression.ClassDeclaration;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompileController {

    @PostMapping("/compile")
    public CompileResult compile(@RequestBody Map<String, String> body) {

        String code = body.get("code");
        if (code == null || code.isBlank()) {
            return new CompileResult(
                    false,
                    List.of("No code provided"),
                    List.of()
            );
        }

        StringCompiler compiler = new StringCompiler();
        List<ClassDeclaration> classes = compiler.compileString(code);

        boolean success =
                classes != null &&
                        classes.stream().allMatch(cd -> cd.semanticErrors.isEmpty());

        return new CompileResult(
                success,
                compiler.getSemanticErrors(),
                classes
        );
    }
}
