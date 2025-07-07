package model.Expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassDeclaration extends Expression {
	public List<Statement> statements;
	public String className;
	public String superClass;

	public ClassDeclaration(String className) {
		this.className = className;
		this.statements = new ArrayList<>();
	}

	public Set<String> getVariables() {
		Set<String> res = new HashSet<>();
		for (Statement s : statements) {
			res.addAll(s.getVariables());
		}
		return res;
	}

	public ClassDeclaration(String className, String superClass) {
		this(className);
		this.superClass = superClass;
	}

	@Override
	public String toString() {
		return "Class " + className + (this.superClass == null ? "" : "extends " + superClass) + "{" + this.statements
				+ "}";
	}
}
