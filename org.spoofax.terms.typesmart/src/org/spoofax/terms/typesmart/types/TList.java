package org.spoofax.terms.typesmart.types;

import com.google.common.collect.ImmutableClassToInstanceMap;
import mb.terms.ITerm;
import mb.terms.ITermAttachment;
import mb.terms.ITermList;
import org.spoofax.terms.typesmart.TypesmartContext;

import java.util.Collections;
import java.util.List;

public class TList extends SortType {
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

    @Override public boolean matches(ITerm t, TypesmartContext context) {
        if(t.getTermKind() == ITerm.TermKind.List) {
            for(ITerm sub : ((ITermList) t).children())
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

    @Override
    public String constructor() {
        return "TList";
    }

    @Override
    public List<ITerm> children() {
        return Collections.unmodifiableList(Collections.singletonList(elemType));
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
