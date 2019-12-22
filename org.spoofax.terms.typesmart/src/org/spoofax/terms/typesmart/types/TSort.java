package org.spoofax.terms.typesmart.types;

import com.google.common.collect.ImmutableClassToInstanceMap;
import mb.terms.ITerm;
import mb.terms.ITermAttachment;
import mb.terms.TermString;
import org.spoofax.terms.typesmart.TypesmartContext;
import org.spoofax.terms.typesmart.TypesmartSortAttachment;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TSort extends SortType {
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

    @Override public boolean matches(ITerm t, TypesmartContext context) {
        if(t.getTermKind() == ITerm.TermKind.String) {
            return TLexical.instance.subtypeOf(this, context);
        }


        SortType[] sortAlternatives = TypesmartSortAttachment.getSorts(t);
        if(sortAlternatives != null)
            for(SortType sort : sortAlternatives)
                if(sort.subtypeOf(this, context))
                    return true;

        Set<SortType> injecteds = context.getReverseInjectionsClosure().get(this);
        if (injecteds != null)
            for(SortType injected : injecteds)
                if(injected.matches(t,  context))
                    return true;

        return false;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        return this.equals(t) || t == TAny.instance || context.isInjection(this, t);
    }

    @Override
    public String constructor() {
        return "TSort";
    }

    @Override
    public List<ITerm> children() {
        TermString string = TermString.of(sort, Collections.emptyList(), ImmutableClassToInstanceMap.of());
        return Collections.unmodifiableList(Collections.singletonList(string));
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
