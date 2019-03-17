package mb.terms;

import io.usethesource.capsule.Map;

public interface ITermMap extends ITerm {
    TermKind termKind = TermKind.Map;

    Map.Immutable<ITerm, ITerm> map();

    @Override
    default TermKind getTermKind() {
        return termKind;
    }
}
