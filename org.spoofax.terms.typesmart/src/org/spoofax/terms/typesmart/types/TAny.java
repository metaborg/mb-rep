package org.spoofax.terms.typesmart.types;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public class TAny implements SortType {
    private static final long serialVersionUID = -4577501316984065256L;

    private TAny() {
    }

    public final static TAny instance = new TAny();

    @Override public String toString() {
        return SortType.ANY_SORT;
    }

    @Override public boolean matches(IStrategoTerm t, TypesmartContext context) {
        return true;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        return true;
    }

}
