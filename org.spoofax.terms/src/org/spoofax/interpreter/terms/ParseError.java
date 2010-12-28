package org.spoofax.interpreter.terms;

public class ParseError extends RuntimeException {

	private static final long serialVersionUID = 7598516227302756592L;

	public ParseError(String m) {
        super(m);
    }
}
