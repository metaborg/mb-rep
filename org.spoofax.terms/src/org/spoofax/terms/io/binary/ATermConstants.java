package org.spoofax.terms.io.binary;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class ATermConstants {

    public static final int AT_APPL = 1;

    public static final int AT_INT = 2;

    public static final int AT_REAL = 3;

    public static final int AT_LIST = 4;

    public static int ATermTypeForTerm(IStrategoTerm term) {
    	switch (term.getType()) {
    		case APPL:  case STRING: case TUPLE: return AT_APPL;
    		case INT: return AT_INT;
    		case REAL: return AT_REAL;
    		case LIST: return AT_LIST;
    		default:
	            throw new RuntimeException("Unknown term type '"
	                    + term.getClass().getName()
	                    + "', cannot convert to ATerm type");
        }
    }

}
