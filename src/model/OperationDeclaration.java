package model;

import java.util.ArrayList;
import java.util.List;

public class OperationDeclaration extends Expression {
    public String name;
    public List<Assignment> assignments;

    public OperationDeclaration(String name) {
        this.name = name;
        this.assignments = new ArrayList<>();
    }

    public void addAssignment(Assignment assignment) {
        this.assignments.add(assignment);
    }
}