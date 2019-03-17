package mb.terms;

import java.util.List;

public interface ITermApplication extends ITerm {
    TermKind termKind = TermKind.Application;

    String constructor();

    List<ITerm> children();

    @Override
    default ITermApplication withAnnotations(ITerm... annotations) {
        return (ITermApplication) ITerm.super.withAnnotations(annotations);
    }

    @Override
    default ITermApplication withAttachment(Object attachment) {
        return (ITermApplication) ITerm.super.withAttachment(attachment);
    }

    @Override
    default TermKind getTermKind() {
        return termKind;
    }
}
