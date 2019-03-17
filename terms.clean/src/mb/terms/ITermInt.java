package mb.terms;

public interface ITermInt extends ITerm {
    TermKind termKind = TermKind.Int;

    int value();

    @Override
    default ITermInt withAnnotations(ITerm... annotations) {
        return (ITermInt) ITerm.super.withAnnotations(annotations);
    }

    @Override
    default ITermInt withAttachment(Object attachment) {
        return (ITermInt) ITerm.super.withAttachment(attachment);
    }

    @Override
    default TermKind getTermKind() {
        return termKind;
    }
}
