package org.spoofax.terms.typesmart.types;

import java.util.Arrays;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.typesmart.TypesmartContext;

public class TTuple implements SortType {
    private static final long serialVersionUID = 6705241429971917743L;

    private SortType[] elemTypes;

    public TTuple(SortType[] elemTypes) {
        this.elemTypes = elemTypes;
    }

    /*
     * Package protected to prevent modifications.
     */
    SortType[] getElemTypes() {
        return elemTypes;
    }

    @Override public boolean equals(Object obj) {
        return obj instanceof TTuple && Arrays.equals(((TTuple) obj).elemTypes, elemTypes);
    }

    @Override public int hashCode() {
        return Arrays.hashCode(elemTypes);
    }

    @Override public String toString() {
        String elems = Arrays.toString(elemTypes);
        return "Tuple<" + elems.substring(1, elems.length() - 1) + ">";
    }

    @Override public boolean matches(IStrategoTerm t, TypesmartContext context) {
        if(t.getTermType() == 0) {
            IStrategoAppl appl = (IStrategoAppl) t;
            if("".equals(appl.getName()) && elemTypes.length == appl.getSubtermCount()) {
                for(int i = 0; i < elemTypes.length; i++) {
                    if(!elemTypes[i].matches(appl.getSubterm(i), context)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        if(t instanceof TTuple && elemTypes.length == ((TTuple) t).elemTypes.length) {
            SortType[] ts = ((TTuple) t).elemTypes;
            for(int i = 0; i < elemTypes.length; i++) {
                if(!elemTypes[i].subtypeOf(ts[i], context)) {
                    return false;
                }
            }
            return true;
        }
        return t == TAny.instance || context.isInjection(this, t);
    }
}
