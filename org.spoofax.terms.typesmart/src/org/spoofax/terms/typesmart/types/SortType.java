package org.spoofax.terms.typesmart.types;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public interface SortType {
    public boolean matches(IStrategoTerm t, TypesmartContext context);

    public boolean subtypeOf(SortType t, TypesmartContext context);
}
