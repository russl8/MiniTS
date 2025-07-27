package app;

import java.util.HashMap;
import java.util.Map;

import model.Expression.Expression.Type;

public class Utils {

    public static Map<String, Type> copyVarScope(Map<String, Type> vars) {
        return new HashMap<>(vars);
    }

    public static void restoreVarScope(Map<String, Type> currentVars, Map<String, Type> previousScope) {
        currentVars.clear();
        currentVars.putAll(previousScope);
    }
}