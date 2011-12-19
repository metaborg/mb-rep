package org.spoofax.terms.io.binary;

import static org.spoofax.interpreter.terms.IStrategoTerm.APPL;
import static org.spoofax.interpreter.terms.IStrategoTerm.INT;
import static org.spoofax.interpreter.terms.IStrategoTerm.LIST;
import static org.spoofax.interpreter.terms.IStrategoTerm.REAL;
import static org.spoofax.interpreter.terms.IStrategoTerm.STRING;
import static org.spoofax.interpreter.terms.IStrategoTerm.TUPLE;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class ATermConstants {

    public static final int AT_APPL = 1;

    public static final int AT_INT = 2;

    public static final int AT_REAL = 3;

    public static final int AT_LIST = 4;

    public static int ATermTypeForTerm(IStrategoTerm term) {
    	switch (term.getTermType()) {
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
