package org.spoofax.terms.typesmart.types;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public interface SortType extends Serializable {
    public final static SortType LEXICAL_SORT = new TSort("String");
    
    public boolean matches(IStrategoTerm t, TypesmartContext context);

    public boolean subtypeOf(SortType t, TypesmartContext context);
}
