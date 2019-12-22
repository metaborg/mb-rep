package org.spoofax.terms.typesmart.types;

import com.google.common.collect.ImmutableClassToInstanceMap;
import mb.terms.ITerm;
import mb.terms.ITermAttachment;
import org.spoofax.terms.typesmart.TypesmartContext;

import java.util.Collections;
import java.util.List;

public class TLexical extends SortType {
    private static final long serialVersionUID = -2366199070240630889L;

    private TLexical() {
    }

    public static TLexical instance = new TLexical();

    @Override public String toString() {
        return SortType.LEXICAL_SORT;
    }

    @Override public boolean matches(ITerm t, TypesmartContext context) {
        return t.getTermKind() == ITerm.TermKind.String;
    }

    @Override public boolean subtypeOf(SortType t, TypesmartContext context) {
        if(t == this || t == TAny.instance)
            return true;
        if(t instanceof TSort)
            return context.getLexicals().contains(t);
        return false;
    }

    @Override
    public String constructor() {
        return "TLexical";
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
