package org.spoofax.terms.typesmart.types;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public interface SortType extends Serializable {
    public final static String LEXICAL_SORT = "String";
    public final static String ANY_SORT = "T_Any";
    
    public boolean matches(IStrategoTerm t, TypesmartContext context);

    public boolean subtypeOf(SortType t, TypesmartContext context);
}
