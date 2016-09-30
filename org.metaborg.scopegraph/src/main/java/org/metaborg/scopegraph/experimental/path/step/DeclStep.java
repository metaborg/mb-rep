package org.metaborg.scopegraph.experimental.path.step;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.DeclPath;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

public final class DeclStep implements DeclPath {

    private static final long serialVersionUID = 1176168741646481567L;

    private final IScope scope;
    private final IOccurrence declaration;

    public DeclStep(IScope scope, IOccurrence declaration) {
        this.scope = scope;
        this.declaration = declaration;
    }

    @Override public IScope scope() {
        return scope;
    }

    @Override public IOccurrence declaration() {
        return declaration;
    }

    @Override public int size() {
        return 1;
    }

    @Override public PSet<IScope> scopes() {
        return HashTreePSet.singleton(scope);
    }

    @Override public PSet<IOccurrence> references() {
        return HashTreePSet.empty();
    }

}
