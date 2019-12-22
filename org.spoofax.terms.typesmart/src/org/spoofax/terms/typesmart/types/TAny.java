package org.spoofax.terms.typesmart.types;

import com.google.common.collect.ImmutableClassToInstanceMap;
import mb.terms.ITerm;
import mb.terms.ITermAttachment;
import org.spoofax.terms.typesmart.TypesmartContext;

import java.util.Collections;
import java.util.List;

public class TAny extends SortType {
    private static final long serialVersionUID = -4577501316984065256L;

    private TAny() {
    }

    public final static TAny instance = new TAny();

    @Override public String toString() {
        return SortType.ANY_SORT;
    }

    @Override public boolean matches(ITerm t, TypesmartContext context) {
        return true;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        return true;
    }

    @Override
    public String constructor() {
        return "TAny";
    }

    @Override
    public List<ITerm> children() {
        return Collections.unmodifiableList(Collections.emptyList());
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
