package org.metaborg.scopegraph.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.CyclicPathException;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IScopePath;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.scopegraph.path.Paths;
import org.metaborg.scopegraph.path.ScopeMismatchPathException;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;


public class ScopeScopePathConcat implements IScopePath {

    private static final long serialVersionUID = 5910491421765037299L;

    private final IScopePath left;
    private final IScopePath right;
    private final int size;

    public ScopeScopePathConcat(IScopePath left, IScopePath right) throws PathException {
        if (!left.targetScope().equals(right.sourceScope())) {
            throw new ScopeMismatchPathException();
        }
        if (Paths.isCyclic(left, right, left.targetScope())) {
            throw new CyclicPathException();
        }
        this.left = left;
        this.right = right;
        this.size = left.size() + 1 + right.size();
    }

    @Override public IScope sourceScope() {
        return left.sourceScope();
    }

    @Override public IScope targetScope() {
        return right.targetScope();
    }

    @Override public int size() {
        return size;
    }

    @Override public PSet<IScope> scopes() {
        return PathConcat.union(left.scopes(), right.scopes());
    }

    @Override public PSet<IOccurrence> references() {
        return PathConcat.union(left.references(), right.references());
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit(this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators.concat(left.iterator(), right.iterator());
    }

}