package org.spoofax.terms.typesmart.types;

import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;
import org.spoofax.terms.typesmart.TypesmartSortAttachment;

public class TSort implements SortType {
    private static final long serialVersionUID = 1784763837646076585L;

    private String sort;

    public TSort(String sort) {
        this.sort = sort;
    }

    public String getSort() {
        return sort;
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof TSort && ((TSort) obj).sort.equals(sort);
    }

    @Override public int hashCode() {
        return sort.hashCode();
    }

    @Override public String toString() {
        return sort;
    }

    @Override public boolean matches(IStrategoTerm t, TypesmartContext context) {
        if(t.getTermType() == IStrategoTerm.STRING)
            return TLexical.instance.subtypeOf(this, context);

        SortType[] sortAlternatives = TypesmartSortAttachment.getSorts(t);
        if(sortAlternatives != null)
            for(SortType sort : sortAlternatives)
                if(sort.subtypeOf(this, context))
                    return true;

        return false;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        if(t instanceof TSort) {
            String ts = ((TSort) t).sort;

            if(sort.equals(ts))
                return true;

            Set<SortType> injectedInto = context.getInjectionsClosure().get(this);
            return injectedInto != null && injectedInto.contains(t);
        }

        return false;
    }
}
