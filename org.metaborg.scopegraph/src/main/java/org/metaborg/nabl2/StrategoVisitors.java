package org.metaborg.nabl2;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class StrategoVisitors {

    private StrategoVisitors() {
    }

    public static <T> T accept(IStrategoTerm term, IStrategoVisitor<T> visitor) {
        switch (term.getTermType()) {
        case IStrategoTerm.APPL:
            return visitor.visit((IStrategoAppl) term);
        case IStrategoTerm.LIST:
            return visitor.visit((IStrategoList) term);
        case IStrategoTerm.TUPLE:
            return visitor.visit((IStrategoTuple) term);
        case IStrategoTerm.INT:
            return visitor.visit((IStrategoInt) term);
        case IStrategoTerm.REAL:
            return visitor.visit((IStrategoReal) term);
        case IStrategoTerm.STRING:
            return visitor.visit((IStrategoString) term);
        default:
            throw new IllegalArgumentException("type of " + term + "is not supported.");
        }
    }

}
