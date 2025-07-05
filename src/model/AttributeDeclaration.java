package model;

public class AttributeDeclaration extends Expression {
    public String name;
    public String type;

    public AttributeDeclaration(String name, String type) {
        this.name = name;
        this.type = type;
    }
}