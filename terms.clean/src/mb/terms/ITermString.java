package mb.terms;

public interface ITermString extends ITerm {
    TermKind termKind = TermKind.String;

    String value();

    @Override
    default TermKind getTermKind() {
        return termKind;
    }
}
