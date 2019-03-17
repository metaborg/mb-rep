package mb.terms;

import com.google.common.collect.ImmutableClassToInstanceMap;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public interface ITermList extends ITerm, Iterable<ITerm> {
    TermKind termKind = TermKind.List;

    @Override
    default TermKind getTermKind() {
        return termKind;
    }

    @Override
    ITermList withAnnotations(Iterable<? extends ITerm> annotations);

    @Override
    ITermList withAttachments(ImmutableClassToInstanceMap<Object> attachments);

    @Override
    default ITermList withAttachment(Object attachment) {
        return (ITermList) ITerm.super.withAttachment(attachment);
    }

    @Override
    default ITermList withAnnotations(ITerm... annotations) {
        return (ITermList) ITerm.super.withAnnotations(annotations);
    }

    List<ITerm> children();

    int offset();

    ITermList withOffset(int value);

    default ITerm head() {
        return children().get(0);
    }

    default ITermList tail() {
        return this.withOffset(offset() + 1).withAnnotations(/* none */).withAttachments(AbstractTermFactory.NO_ATTACHMENTS);
    }

    default int size() {
        return children().size() - offset();
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    @Nonnull
    default Iterator<ITerm> iterator() {
        return new Iter(this.children(), offset());
    }

    default Iterator<ITermList> tails() {
        return new TailIter(this);
    }

    final class Iter implements Iterator<ITerm> {
        private final List<ITerm> children;
        private int offset;

        Iter(List<ITerm> children, int offset) {
            this.children = children;
            this.offset = offset;
        }

        @Override
        public boolean hasNext(){
            return children.size() >= offset;
        }

        @Override
        public ITerm next(){
            ITerm result = children.get(offset);
            offset++;
            return result;
        }
    }

    final class TailIter implements Iterator<ITermList> {
        private ITermList list;

        TailIter(ITermList list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public ITermList next() {
            ITermList result = list;
            list = list.tail();
            return result;
        }
    }
}
