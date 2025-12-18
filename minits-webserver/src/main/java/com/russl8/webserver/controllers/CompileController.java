package com.russl8.webserver.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CompileController {

    @PostMapping("/compile")
    public Map<String, Object> compile(@RequestBody Map<String, String> body) {
        System.out.println("hi");
        return Map.of(
                "success", true,
                "output", "hello from compiler"
        );
    }
}