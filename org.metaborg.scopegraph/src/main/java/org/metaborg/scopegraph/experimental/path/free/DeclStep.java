package org.metaborg.scopegraph.experimental.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.IDeclStep;
import org.metaborg.scopegraph.experimental.path.IPathVisitor;
import org.metaborg.scopegraph.experimental.path.IStep;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;

public final class DeclStep implements IDeclStep {

    private static final long serialVersionUID = 1176168741646481567L;

    private final IScope scope;
    private final IOccurrence declaration;

    public DeclStep(IScope scope, IOccurrence declaration) {
        this.scope = scope;
        this.declaration = declaration;
    }

    @Override public IScope sourceScope() {
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

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit(this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators.<IStep> singletonIterator(this);
    }

}