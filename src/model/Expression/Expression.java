package model.Expression;

import java.util.Set;

public abstract class Expression extends Statement{
	public abstract Set<String> getVariables();
}
