package org.metaborg.scopegraph.experimental.path.step;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.RefPath;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

public final class RefStep implements RefPath {

    private static final long serialVersionUID = 4409541109071389538L;

    private final IOccurrence reference;
    private final IScope scope;

    public RefStep(IOccurrence reference, IScope scope) {
        this.reference = reference;
        this.scope = scope;
    }

    @Override public IOccurrence reference() {
        return reference;
    }

    @Override public IScope scope() {
        return scope;
    }

    @Override public int size() {
        return 1;
    }

    @Override public PSet<IScope> scopes() {
        return HashTreePSet.singleton(scope);
    }

    @Override public PSet<IOccurrence> references() {
        return HashTreePSet.singleton(reference);
    }

}
