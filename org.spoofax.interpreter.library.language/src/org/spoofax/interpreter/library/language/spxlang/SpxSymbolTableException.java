package org.spoofax.interpreter.library.language.spxlang;

/**
 * Custom Exception thrown to indicate symbol table lookup has been failed.  
 * 
 * @author Md. Adil Akhter
 * Created On : Sep 11, 2011
 */
public class SpxSymbolTableException extends Exception {

	private static final long serialVersionUID = 2412790462608500241L;

	public SpxSymbolTableException() {
		super();
	}

	public SpxSymbolTableException(String message, Throwable cause) {
		super(message, cause);
	}

	public SpxSymbolTableException(String message) {
		super(message);
	}

	public SpxSymbolTableException(Throwable cause) {
		super(cause);
	}
}
