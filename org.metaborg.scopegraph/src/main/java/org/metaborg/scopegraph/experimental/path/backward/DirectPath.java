package org.metaborg.scopegraph.experimental.path.backward;

import java.util.Iterator;

import org.metaborg.scopegraph.experimental.ILabel;
import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.IDeclPath;
import org.metaborg.scopegraph.experimental.path.IDirectStep;
import org.metaborg.scopegraph.experimental.path.IPathVisitor;
import org.metaborg.scopegraph.experimental.path.IStep;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.util.iterators.Iterators2;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

public class DirectPath implements IDirectStep, IDeclPath {

    private static final long serialVersionUID = -7428671792304638252L;

    private final IScope sourceScope;
    private final ILabel label;
    private final IDeclPath tail;

    public DirectPath(IScope sourceScope, ILabel label, IDeclPath tail) throws PathException {
        if (tail.scopes().contains(sourceScope)) {
            throw new CyclicPathException();
        }
        this.sourceScope = sourceScope;
        this.label = label;
        this.tail = tail;
    }

    @Override public IScope sourceScope() {
        return sourceScope;
    }

    @Override public IScope targetScope() {
        return tail.sourceScope();
    }

    @Override public int size() {
        return 1 + tail.size();
    }

    @Override public PSet<IScope> scopes() {
        return tail.scopes().plus(sourceScope);
    }

    @Override public PSet<IOccurrence> references() {
        return HashTreePSet.empty();
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit((IDeclPath) this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators2.cons(this, tail.iterator());
    }

    @Override public ILabel label() {
        return label;
    }

    @Override public IOccurrence declaration() {
        return tail.declaration();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sourceScope.hashCode();
        result = prime * result + label.hashCode();
        result = prime * result + tail.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DirectPath other = (DirectPath) obj;
        if (!sourceScope.equals(other.sourceScope))
            return false;
        if (!label.equals(other.label))
            return false;
        if (!tail.equals(other.tail))
            return false;
        return true;
    }

    @Override public String toString() {
        return sourceScope + " . E(" + String.valueOf(label) + ") . " + tail;
    }

}
