package org.spoofax.terms.clean;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.spoofax.terms.clean.AbstractCTFactory.NO_ATTACHMENTS;

@SuppressWarnings({"unused", "WeakerAccess"})
@Value.Immutable
abstract class AbstractCTList implements ICleanTerm {
    public static final TermKind termKind = TermKind.List;

    public abstract List<ICleanTerm> children();

    public abstract List<List<ICleanTerm>> consNilAnnotations();

    @Value.Auxiliary
    public abstract List<ImmutableClassToInstanceMap<ICleanTermAttachment>> consNilAttachments();

    public ImmutableClassToInstanceMap<ICleanTermAttachment> attachments() {
        try {
            return consNilAttachments().get(offset());
        } catch (IndexOutOfBoundsException e) {
            return AbstractCTFactory.NO_ATTACHMENTS;
        }
    }

    public List<ICleanTerm> annotations() {
        try {
            return consNilAnnotations().get(offset());
        } catch (IndexOutOfBoundsException e) {
            return Collections.emptyList();
        }
    }

    @Value.Parameter(false)
    @Value.Default
    public int offset() {
        return 0;
    }

    @Value.Check
    protected void check() {
        Preconditions.checkState(!(children().size() <= offset()),
                "'offset' should be smaller/equal to the amount of children");
    }

    @Override
    public TermKind getTermKind() {
        return termKind;
    }

    public abstract CTList withOffset(int value);

    @Override
    public CTList withAnnotations(Iterable<? extends ICleanTerm> annotations) {
        List<List<ICleanTerm>> newAnnotations = new ArrayList<>(consNilAnnotations());
        ArrayList<ICleanTerm> safeList = new ArrayList<>();
        for (ICleanTerm element : annotations) {
            safeList.add(element);
        }
        newAnnotations.set(0, Collections.unmodifiableList(safeList));
        return CTList.of(children(), newAnnotations, consNilAttachments());
    }

    @Override
    public CTList withAttachments(ImmutableClassToInstanceMap<ICleanTermAttachment> attachments) {
        List<ImmutableClassToInstanceMap<ICleanTermAttachment>> newAttachments = new ArrayList<>(consNilAttachments());
        newAttachments.set(0, attachments);
        return CTList.of(children(), consNilAnnotations(), newAttachments);
    }

    @Override
    public CTList withAnnotations(ICleanTerm... annotations) {
        Preconditions.checkState(!(children().size() == consNilAnnotations().size() + 1),
                "'consNilAnnotations.size() + 1' should be equal to the amount of children");
        Preconditions.checkState(!(children().size() == consNilAttachments().size() + 1),
                "'consNilAttachments.size() + 1' should be equal to the amount of children");
        return (CTList) ICleanTerm.super.withAnnotations(annotations);
    }

    public ICleanTerm head() {
        return children().get(0);
    }

    public CTList tail() {
        return this.withOffset(offset() + 1).withAnnotations(/* none */).withAttachments(NO_ATTACHMENTS);
    }

    @Override
    public boolean equals(Object another) {
        return this == another || another instanceof CTList && equalTo((CTList) another);
    }

    private boolean equalTo(CTList another) {
        if (hashCode() != another.hashCode()) return false;
        if (children().size() - offset() != another.children().size() - another.offset()) return false;
        for (int i = offset(), j = another.offset(); i < children().size(); i++, j++) {
            if (!Objects.equals(children().get(i), another.children().get(j))) return false;
        }
        for (int i = offset(), j = another.offset(); i < consNilAnnotations().size(); i++, j++) {
            if (!Objects.equals(consNilAnnotations().get(i), another.consNilAnnotations().get(j))) return false;
        }
        return true;
    }
}
