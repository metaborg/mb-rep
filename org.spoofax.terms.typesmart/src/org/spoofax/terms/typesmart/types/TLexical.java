package org.spoofax.terms.typesmart.types;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public class TLexical implements SortType {
    private static final long serialVersionUID = -2366199070240630889L;

    private TLexical() {
    }

    public static TLexical instance = new TLexical();

    @Override public String toString() {
        return SortType.LEXICAL_SORT;
    }

    @Override public boolean matches(IStrategoTerm t, TypesmartContext context) {
        return t.getTermType() == IStrategoTerm.STRING;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        if(t == this || t == TAny.instance)
            return true;
        if(t instanceof TSort)
            return context.getLexicals().contains(t);
        return false;
    }
}
