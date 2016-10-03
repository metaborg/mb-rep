package org.metaborg.scopegraph.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IRefStep;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;

public final class RefStep implements IRefStep {

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

    @Override public IScope targetScope() {
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

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit(this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators.<IStep> singletonIterator(this);
    }

}
