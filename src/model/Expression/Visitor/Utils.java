package model.Expression.Visitor;

import java.util.List;
import java.util.Map;

import model.Value;
import model.Expression.ClassDeclaration;
import model.Expression.Expression;
import model.Expression.Expression.Type;

public class Utils {
	public static ClassDeclaration getClassByClassName(List<ClassDeclaration> classes, String className) {
		for (ClassDeclaration cd : classes) {
			if (cd.className.equals(className)) {
				return cd;
			}
		}
		return null;
	}

	public static String getErrorLocation(Expression e) {
		String errorLocation = "[" + e.getLine() + ", " + e.getCol() + "]";
		return errorLocation;
	}

	public static void deepCopyEvaluatedVars(Map<String, Value> superClassVars, Map<String, Value> currentClassVars) {
		for (String key : superClassVars.keySet()) {
			Value superValue = superClassVars.get(key);

			Value copiedValue = new Value(superValue.type);
			copiedValue.setValue(superValue.getValueAsObject());

			currentClassVars.put(key, copiedValue);
		}
	}

	public static Type getItemTypeFromListType(Type listType) {
		Type itemType;
		switch (listType) {
		case LIST_INT:
			itemType = Type.INT;
			break;
		case LIST_BOOL:
			itemType = Type.BOOL;
			break;
		case LIST_CHAR:
			itemType = Type.CHAR;
			break;
		default:
			itemType = Type.NONE;
			System.err.println("Unexpected type: " + listType);
		}
		return itemType;
	}

}
