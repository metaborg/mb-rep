package mb.terms;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Value.Immutable
abstract class AbstractTermList implements ITerm {
    @SuppressWarnings("WeakerAccess")
    public static final TermKind termKind = TermKind.List;

    public abstract List<ITerm> children();

    public abstract List<List<ITerm>> consNilAnnotations();

    @Value.Auxiliary
    public abstract List<ImmutableClassToInstanceMap<ITermAttachment>> consNilAttachments();

    public ImmutableClassToInstanceMap<ITermAttachment> attachments() {
        try {
            return consNilAttachments().get(offset());
        } catch (IndexOutOfBoundsException e) {
            return AbstractTermFactory.NO_ATTACHMENTS;
        }
    }

    public List<ITerm> annotations() {
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

    public abstract TermList withOffset(int value);

    @Override
    public AbstractTermList withAnnotations(Iterable<? extends ITerm> annotations) {
        List<List<ITerm>> newAnnotations = new ArrayList<>(consNilAnnotations());
        ArrayList<ITerm> safeList = new ArrayList<>();
        for (ITerm element : annotations) {
            safeList.add(element);
        }
        newAnnotations.set(0, Collections.unmodifiableList(safeList));
        return TermList.of(children(), newAnnotations, consNilAttachments());
    }

    @Override
    public AbstractTermList withAttachments(ImmutableClassToInstanceMap<ITermAttachment> attachments) {
        List<ImmutableClassToInstanceMap<ITermAttachment>> newAttachments = new ArrayList<>(consNilAttachments());
        newAttachments.set(0, attachments);
        return TermList.of(children(), consNilAnnotations(), newAttachments);
    }

    @Override
    public AbstractTermList withAnnotations(ITerm... annotations) {
        Preconditions.checkState(!(children().size() == consNilAnnotations().size() + 1),
                "'consNilAnnotations.size() + 1' should be equal to the amount of children");
        Preconditions.checkState(!(children().size() == consNilAttachments().size() + 1),
                "'consNilAttachments.size() + 1' should be equal to the amount of children");
        return (TermList) ITerm.super.withAnnotations(annotations);
    }

    public ITerm head() {
        return children().get(0);
    }

    public AbstractTermList tail() {
        return this.withOffset(offset() + 1).withAnnotations(/* none */).withAttachments(AbstractTermFactory.NO_ATTACHMENTS);
    }

    @Override
    public boolean equals(Object another) {
        return this == another || another instanceof TermList && equalTo((TermList) another);
    }

    private boolean equalTo(TermList another) {
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
