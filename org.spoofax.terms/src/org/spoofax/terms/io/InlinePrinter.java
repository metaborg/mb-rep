package org.spoofax.terms.io;

import org.spoofax.interpreter.terms.ITermPrinter;

public class InlinePrinter implements ITermPrinter {
	private final StringBuilder out;
	
	public InlinePrinter() {
		out = new StringBuilder();
	}
	
	public String getString() {
		return out.toString();
	}

	public void print(String string) {
		/* UNDONE: don't break empty strategostrings
	    if (string.matches("^ *$")) {
		    return ;
		}
		*/
		out.append(string);
	}

	public void indent(int i) {
		// Do nothing
	}

	public void nextIndentOff() {
		// Do nothing
	}

	public void outdent(int i) {
		// Do nothing
	}
	
	@Override
	public String toString() {
	    return getString();
	}
	
	public void reset() {
	    out.setLength(0);
	}

	public void println(String string, boolean b) {
	    /*
		if (string.matches("^ *$")) {
			return ;
		}
		*/
		out.append(string);
	}

	public void println(String string) {
		out.append(string);
	}
}
