package org.spoofax.terms.typesmart.types;

import com.google.common.collect.ImmutableClassToInstanceMap;
import mb.terms.ITerm;
import mb.terms.ITermApplication;
import mb.terms.ITermAttachment;
import org.spoofax.terms.typesmart.TypesmartContext;

import java.util.Collections;
import java.util.List;

public class TOption extends SortType {
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

    @Override public boolean matches(ITerm t, TypesmartContext context) {
        if(t.getTermKind() == ITerm.TermKind.Application) {
            ITermApplication appl = (ITermApplication) t;
            if("None".equals(appl.constructor()) && appl.children().size() == 0) {
                return true;
            }
            if("Some".equals(appl.constructor()) && appl.children().size() == 1) {
                return elemType.matches(appl.children().get(0), context);
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

    @Override
    public String constructor() {
        return "TOption";
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
