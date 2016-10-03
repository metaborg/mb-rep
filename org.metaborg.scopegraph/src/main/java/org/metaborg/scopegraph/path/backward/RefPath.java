package org.metaborg.scopegraph.path.backward;

import java.util.Iterator;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IRefStep;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.OccurrenceMismatchPathException;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.scopegraph.path.RecursivePathException;
import org.metaborg.util.iterators.Iterators2;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

public class RefPath implements IRefStep, IFullPath {

    private static final long serialVersionUID = 4897828115734071046L;

    private final IOccurrence reference;
    private final IDeclPath tail;

    public RefPath(IOccurrence reference, IDeclPath tail) throws PathException {
        if (tail.references().contains(reference)) {
            throw new RecursivePathException();
        }
        if (!reference.matches(tail.declaration())) {
            throw new OccurrenceMismatchPathException();
        }
        this.reference = reference;
        this.tail = tail;
    }

    @Override public IOccurrence reference() {
        return reference;
    }

    @Override public IScope targetScope() {
        return tail.sourceScope();
    }

    @Override public int size() {
        return 1 + tail.size();
    }

    @Override public PSet<IScope> scopes() {
        return tail.scopes();
    }

    @Override public PSet<IOccurrence> references() {
        return HashTreePSet.singleton(reference);
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit((IFullPath) this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators2.cons(this, tail.iterator());
    }

    @Override public IOccurrence declaration() {
        return tail.declaration();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + reference.hashCode();
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
        RefPath other = (RefPath) obj;
        if (!reference.equals(other.reference))
            return false;
        if (!tail.equals(other.tail))
            return false;
        return true;
    }

    @Override public String toString() {
        return "R(" + reference + ") . " + tail;
    }

}