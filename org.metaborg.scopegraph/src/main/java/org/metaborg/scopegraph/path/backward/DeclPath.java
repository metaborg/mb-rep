package org.metaborg.scopegraph.path.backward;

import java.util.Iterator;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.IDeclStep;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;

public class DeclPath implements IDeclStep {

    private static final long serialVersionUID = -113731325988338668L;

    private final IScope sourceScope;
    private final IOccurrence declaration;

    public DeclPath(IScope sourceScope, IOccurrence declaration) {
        this.sourceScope = sourceScope;
        this.declaration = declaration;
    }

    @Override public IScope sourceScope() {
        return sourceScope;
    }

    @Override public IOccurrence declaration() {
        return declaration;
    }

    @Override public int size() {
        return 1;
    }

    @Override public PSet<IScope> scopes() {
        return HashTreePSet.singleton(sourceScope);
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

    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sourceScope.hashCode();
        result = prime * result + declaration.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DeclPath other = (DeclPath) obj;
        if (!declaration.equals(other.declaration))
            return false;
        if (!sourceScope.equals(other.sourceScope))
            return false;
        return true;
    }

    @Override public String toString() {
        return sourceScope + " . D(" + declaration + ")";
    }

}