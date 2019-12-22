package org.spoofax.terms.typesmart.types;

import mb.terms.AbstractTermApplication;
import mb.terms.ITerm;
import org.spoofax.terms.typesmart.TypesmartContext;

import java.io.Serializable;

public abstract class SortType extends AbstractTermApplication implements Serializable {
    public static final String LEXICAL_SORT = "String";
    public static final String ANY_SORT = "T_Any";
    
    public abstract boolean matches(ITerm t, TypesmartContext context);

    public abstract boolean subtypeOf(SortType t, TypesmartContext context);
}
