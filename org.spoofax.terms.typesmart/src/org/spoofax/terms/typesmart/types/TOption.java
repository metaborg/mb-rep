package org.spoofax.terms.typesmart.types;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public class TOption implements SortType {
    private static final long serialVersionUID = 5629000565986671549L;

    private SortType elemType;

    public TOption(SortType elemType) {
        this.elemType = elemType;
    }

    public SortType getElemType() {
        return elemType;
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof TOption && ((TOption) obj).elemType.equals(elemType);
    }

    @Override public int hashCode() {
        return elemType.hashCode() * 31;
    }

    @Override public String toString() {
        return "Option<" + elemType + ">";
    }

    @Override public boolean matches(IStrategoTerm t, TypesmartContext context) {
        if(t.getTermType() == IStrategoTerm.APPL) {
            IStrategoAppl appl = (IStrategoAppl) t;
            if("None".equals(appl.getName()) && appl.getSubtermCount() == 0) {
                return true;
            }
            if("Some".equals(appl.getName()) && appl.getSubtermCount() == 1) {
                return elemType.matches(appl.getSubterm(0), context);
            }
        }
        return false;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        if(t instanceof TOption && elemType.subtypeOf(((TOption) t).elemType, context)) {
            return true;
        }
        return t == TAny.instance || context.isInjection(this, t);
    }
}
