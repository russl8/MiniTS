package model;

import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyErrorListener extends BaseErrorListener {
	private final List<String> errors = new ArrayList<>();

	public boolean hasError() {
		return !errors.isEmpty();
	}

	public List<String> getErrors() {
		return errors;
	}

	@Override
	public void syntaxError(
			Recognizer<?, ?> recognizer,
			Object offendingSymbol,
			int line, int charPositionInLine,
			String msg,
			RecognitionException e
	) {
		List<String> stack = ((Parser) recognizer).getRuleInvocationStack();
		Collections.reverse(stack);

		String tokenText = (offendingSymbol instanceof Token t) ? t.getText() : String.valueOf(offendingSymbol);

		String pretty =
				"Syntax Error! Token \"" + tokenText + "\""
						+ " (line " + line + ", column " + (charPositionInLine + 1) + "): "
						+ msg
						+ " | Rule Stack: " + stack;

		errors.add(pretty);
	}
}
