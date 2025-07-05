package model;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclaration extends Expression {
    public String className;
    public List<AttributeDeclaration> attributes;
    public List<OperationDeclaration> operations;

    public ClassDeclaration() {
        this.attributes = new ArrayList<>();
        this.operations = new ArrayList<>();
    }

    public void addAttribute(AttributeDeclaration attr) {
        this.attributes.add(attr);
    }

    public void addOperation(OperationDeclaration op) {
        this.operations.add(op);
    }
}