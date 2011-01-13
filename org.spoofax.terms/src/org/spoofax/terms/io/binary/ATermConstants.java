package org.spoofax.terms.io.binary;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class ATermConstants {

    public static final int AT_APPL = 1;

    public static final int AT_INT = 2;

    public static final int AT_REAL = 3;

    public static final int AT_LIST = 4;

    public static int ATermTypeForTerm(IStrategoTerm term) {
        if (term instanceof IStrategoAppl) {
            return AT_APPL;
        } else if (term instanceof IStrategoString) {
            return AT_APPL;
        } else if (term instanceof IStrategoTuple) {
            return AT_APPL;
        } else if (term instanceof IStrategoInt) {
            return AT_INT;
        } else if (term instanceof IStrategoReal) {
            return AT_REAL;
        } else if (term instanceof IStrategoList) {
            return AT_LIST;
        } else {
            throw new RuntimeException("Unknown term type '"
                    + term.getClass().getName()
                    + "', cannot convert to ATerm type");
        }
    }

}
