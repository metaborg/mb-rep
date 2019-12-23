package mb.terms;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public interface ITermListForStratego extends ITerm {
    TermKind termKind = TermKind.List;

    @Override
    default TermKind getTermKind() {
        return termKind;
    }

    @Override
    ITermListForStratego withAnnotations(Iterable<? extends ITerm> annotations);

    @Override
    ITermListForStratego withAttachments(HashMap<Class<?>, Object> attachments);

    @Override
    default ITermListForStratego withAttachment(Object attachment) {
        return (ITermListForStratego) ITerm.super.withAttachment(attachment);
    }

    @Override
    default ITermListForStratego withAnnotations(ITerm... annotations) {
        return (ITermListForStratego) ITerm.super.withAnnotations(annotations);
    }

    List<ITerm> children();

    int offset();

    ITermListForStratego withOffset(int value);

    default ITerm head() {
        return children().get(0);
    }

    default ITermListForStratego tail() {
        return this.withOffset(offset() + 1).withAnnotations(/* none */).withAttachments(AbstractTermFactory.noAttachments());
    }

    default int size() {
        return children().size() - offset();
    }

    default boolean isEmpty() {
        return size() == 0;
    }

    default Iterator<ITermListForStratego> tails() {
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

    final class TailIter implements Iterator<ITermListForStratego> {
        private ITermListForStratego list;

        TailIter(ITermListForStratego list) {
            this.list = list;
        }

        @Override
        public boolean hasNext() {
            return !list.isEmpty();
        }

        @Override
        public ITermListForStratego next() {
            ITermListForStratego result = list;
            list = list.tail();
            return result;
        }
    }
}
