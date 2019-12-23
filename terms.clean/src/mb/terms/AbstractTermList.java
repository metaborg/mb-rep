package mb.terms;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableClassToInstanceMap;
import org.immutables.value.Value;

import java.util.*;

@SuppressWarnings({"unused", "WeakerAccess"})
@Value.Immutable
public abstract class AbstractTermList implements ITermList {
    @Override
    public abstract List<ITerm> children();

    protected abstract List<List<ITerm>> consNilAnnotations();

    @Value.Auxiliary
    protected abstract List<HashMap<Class<?>, Object>> consNilAttachments();

    @Override
    public HashMap<Class<?>, Object> attachments() {
        return consNilAttachments().get(offset());
    }

    @Override
    public List<ITerm> annotations() {
        return consNilAnnotations().get(offset());
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
        Preconditions.checkState(!(children().size() == consNilAnnotations().size() + 1),
                "'consNilAnnotations.size() + 1' should be equal to the amount of children");
        Preconditions.checkState(!(children().size() == consNilAttachments().size() + 1),
                "'consNilAttachments.size() + 1' should be equal to the amount of children");
    }

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
    public AbstractTermList withAttachments(HashMap<Class<?>, Object> attachments) {
        List<HashMap<Class<?>, Object>> newAttachments = new ArrayList<>(consNilAttachments());
        newAttachments.set(0, attachments);
        return TermList.of(children(), consNilAnnotations(), newAttachments);
    }

    @Override
    public boolean equals(Object another) {
        return this == another || another instanceof TermList && equalTo((TermList) another);
    }

    private boolean equalTo(TermList another) {
        if (hashCode() != another.hashCode()) return false;
        if (size() != another.size()) return false;
        for (int i = offset(), j = another.offset(); i < children().size(); i++, j++) {
            if (!Objects.equals(children().get(i), another.children().get(j))) return false;
        }
        for (int i = offset(), j = another.offset(); i < consNilAnnotations().size(); i++, j++) {
            if (!Objects.equals(consNilAnnotations().get(i), another.consNilAnnotations().get(j))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + children().subList(offset(), children().size()).hashCode();
        h += (h << 5) + consNilAnnotations().subList(offset(), children().size()).hashCode();
        h += (h << 5) + offset();
        return h;
    }

}
