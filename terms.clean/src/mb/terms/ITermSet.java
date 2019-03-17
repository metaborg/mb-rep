package mb.terms;

import io.usethesource.capsule.Set;

public interface ITermSet extends ITerm {
    TermKind termKind = TermKind.Set;

    Set.Immutable<ITerm> set();

    @Override
    default TermKind getTermKind() {
        return termKind;
    }
}
