package mb.terms;

public interface ITermFloat extends ITerm {
    TermKind termKind = TermKind.Float;

    float value();

    @Override
    default ITermFloat withAnnotations(ITerm... annotations) {
        return (ITermFloat) ITerm.super.withAnnotations(annotations);
    }

    @Override
    default ITermFloat withAttachment(Object attachment) {
        return (ITermFloat) ITerm.super.withAttachment(attachment);
    }

    @Override
    default TermKind getTermKind() {
        return termKind;
    }
}
