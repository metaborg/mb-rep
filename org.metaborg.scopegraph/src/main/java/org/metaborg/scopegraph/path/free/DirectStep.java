package org.metaborg.scopegraph.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.CyclicPathException;
import org.metaborg.scopegraph.path.IDirectStep;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;


public final class DirectStep implements IDirectStep {

    private static final long serialVersionUID = 8232068669592543089L;

    private final IScope sourceScope;
    private final ILabel label;
    private final IScope targetScope;

    public DirectStep(IScope sourceScope, ILabel label, IScope targetScope) throws PathException {
        if (sourceScope.equals(targetScope)) {
            throw new CyclicPathException();
        }
        this.sourceScope = sourceScope;
        this.label = label;
        this.targetScope = targetScope;
    }

    @Override public IScope sourceScope() {
        return sourceScope;
    }

    @Override public ILabel label() {
        return label;
    }

    @Override public IScope targetScope() {
        return targetScope;
    }

    @Override public int size() {
        return 1;
    }

    @Override public PSet<IScope> scopes() {
        return HashTreePSet.singleton(sourceScope).plus(targetScope);
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
