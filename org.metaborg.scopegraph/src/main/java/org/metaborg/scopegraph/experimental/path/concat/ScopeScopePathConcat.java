package org.metaborg.scopegraph.experimental.path.concat;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.ScopeMismatchPathException;
import org.metaborg.scopegraph.experimental.path.ScopePath;
import org.pcollections.PSet;


public class ScopeScopePathConcat implements ScopePath {

    private static final long serialVersionUID = 5910491421765037299L;

    private final ScopePath left;
    private final ScopePath right;
    private final int size;

    public ScopeScopePathConcat(ScopePath left, ScopePath right) throws PathException {
        if (!left.targetScope().equals(right.sourceScope())) {
            throw new ScopeMismatchPathException();
        }
        if (PathConcat.cyclic(left, right, left.targetScope())) {
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

}