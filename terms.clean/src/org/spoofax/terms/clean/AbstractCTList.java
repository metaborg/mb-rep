package org.spoofax.terms.clean;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import org.spoofax.terms.clean.CTFactory;
import org.spoofax.terms.clean.CTList;

import java.util.Arrays;
import java.util.Objects;

import static org.spoofax.terms.clean.AbstractCTFactory.NO_ATTACHMENTS;

// TODO: consider supporting annotations/attachments on sublists
@Value.Immutable
abstract class AbstractCTList implements ICleanTerm {
    public static final TermKind termKind = TermKind.List;
    public abstract ICleanTerm[] children();
    public abstract ICleanTerm[] annotations();
    @Value.Auxiliary
    public abstract ImmutableClassToInstanceMap<ICleanTermAttachment> attachments();
    @Value.Parameter(false)
    @Value.Default
    public int offset() {
        return 0;
    }

    @Value.Check
    protected void check() {
        Preconditions.checkState(!(children().length < offset()),
                "'offset' should be smaller/equal to the amount of children");
    }

    @Override
    public TermKind getTermKind() {
        return termKind;
    }

    public abstract CTList withOffset(int value);

    public ICleanTerm head() {
        return children()[0];
    }

    public CTList tail() {
        return this.withOffset(offset() + 1).withAnnotations(/* none */).withAttachments(NO_ATTACHMENTS);
    }

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof CTList
                && equalTo((CTList) another);
    }

    private boolean equalTo(CTList another) {
        if (hashCode() != another.hashCode()) return false;
        if (children().length - offset() != another.children().length - another.offset()) return false;
        for(int i = offset(), j = another.offset(); i < children().length; i++, j++) {
            if (!Objects.equals(children()[i], another.children()[j])) return false;
        }
        return Arrays.equals(annotations(), another.annotations());
    }
}
