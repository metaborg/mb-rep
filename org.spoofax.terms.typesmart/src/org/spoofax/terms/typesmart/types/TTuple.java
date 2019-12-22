package org.spoofax.terms.typesmart.types;

import com.google.common.collect.ImmutableClassToInstanceMap;
import mb.terms.ITerm;
import mb.terms.ITermApplication;
import mb.terms.ITermAttachment;
import org.spoofax.terms.typesmart.TypesmartContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TTuple extends SortType {
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

    @Override public boolean matches(ITerm t, TypesmartContext context) {
        if(t.getTermKind() == ITerm.TermKind.Application) {
            ITermApplication appl = (ITermApplication) t;
            if("".equals(appl.constructor()) && elemTypes.length == appl.children().size()) {
                for(int i = 0; i < elemTypes.length; i++) {
                    if(!elemTypes[i].matches(appl.children().get(i), context)) {
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

    @Override
    public String constructor() {
        return "TTuple";
    }

    @Override
    public List<ITerm> children() {
        return Collections.unmodifiableList(Arrays.asList(elemTypes));
    }

    @Override
    public List<ITerm> annotations() {
        return Collections.unmodifiableList(Collections.emptyList());
    }

    @Override
    public ImmutableClassToInstanceMap<ITermAttachment> attachments() {
        return ImmutableClassToInstanceMap.of();
    }

    @Override
    public ITerm withAnnotations(Iterable<? extends ITerm> annotations) {
        return this;
    }

    @Override
    public ITerm withAttachments(ImmutableClassToInstanceMap<ITermAttachment> attachments) {
        return this;
    }
}
