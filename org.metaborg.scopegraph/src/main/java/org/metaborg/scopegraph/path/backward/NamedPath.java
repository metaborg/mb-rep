package org.metaborg.scopegraph.path.backward;

import java.util.Iterator;

import org.metaborg.scopegraph.ILabel;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.CyclicPathException;
import org.metaborg.scopegraph.path.IDeclPath;
import org.metaborg.scopegraph.path.IFullPath;
import org.metaborg.scopegraph.path.INamedStep;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.util.iterators.Iterators2;
import org.pcollections.PSet;

public class NamedPath implements INamedStep, IDeclPath {

    private static final long serialVersionUID = 8711734422648610572L;

    private final IScope sourceScope;
    private final ILabel label;
    private final IFullPath path;
    private final IDeclPath tail;

    public NamedPath(IScope sourceScope, ILabel label, IFullPath path, IDeclPath tail) throws PathException {
        if (tail.scopes().contains(sourceScope)) {
            throw new CyclicPathException();
        }
        this.sourceScope = sourceScope;
        this.label = label;
        this.path = path;
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
        return tail.references().plusAll(path.references());
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

    @Override public IFullPath path() {
        return path;
    }

    @Override public IOccurrence declaration() {
        return tail.declaration();
    }

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sourceScope.hashCode();
        result = prime * result + label.hashCode();
        result = prime * result + path.hashCode();
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
        NamedPath other = (NamedPath) obj;
        if (!sourceScope.equals(other.sourceScope))
            return false;
        if (!label.equals(other.label))
            return false;
        if (!path.equals(other.path))
            return false;
        if (!tail.equals(other.tail))
            return false;
        return true;
    }

    @Override public String toString() {
        return sourceScope + " . N(" + String.valueOf(label) + ", " + path + ") . " + tail;
    }

}
