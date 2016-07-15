package org.spoofax.terms.typesmart.types;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public class TList implements SortType {
    private static final long serialVersionUID = 886205363941392393L;

    private SortType elemType;

    public TList(SortType elemType) {
        this.elemType = elemType;
    }

    public SortType getElemType() {
        return elemType;
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof TList && ((TList) obj).elemType.equals(elemType);
    }

    @Override public int hashCode() {
        return elemType.hashCode() * 31;
    }

    @Override public String toString() {
        return "List<" + elemType + ">";
    }

    @Override public boolean matches(IStrategoTerm t, TypesmartContext context) {
        if(t.getTermType() == IStrategoTerm.LIST) {
            for(IStrategoTerm sub : t)
                if(!elemType.matches(sub, context))
                    return false;
            return true;
        }
        return false;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        if(t instanceof TList && elemType.subtypeOf(((TList) t).elemType, context)) {
            return true;
        }
        return t == TAny.instance || context.isInjection(this, t);
    }
}
