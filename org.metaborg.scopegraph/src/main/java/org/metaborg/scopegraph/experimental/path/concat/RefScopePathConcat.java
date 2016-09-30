package org.metaborg.scopegraph.experimental.path.concat;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.RecursivePathException;
import org.metaborg.scopegraph.experimental.path.RefPath;
import org.metaborg.scopegraph.experimental.path.ScopeMismatchPathException;
import org.metaborg.scopegraph.experimental.path.ScopePath;
import org.pcollections.PSet;


public class RefScopePathConcat implements RefPath {

    private static final long serialVersionUID = 6199966071731432759L;

    private final RefPath left;
    private final ScopePath right;
    private final int size;

    public RefScopePathConcat(RefPath left, ScopePath right) throws PathException {
        if (!left.scope().equals(right.sourceScope())) {
            throw new ScopeMismatchPathException();
        }
        if (PathConcat.cyclic(left, right, left.scope())) {
            throw new CyclicPathException();
        }
        if (PathConcat.recursive(right, left.reference())) {
            throw new RecursivePathException();
        }
        this.left = left;
        this.right = right;
        this.size = left.size() + 1 + right.size();
    }

    @Override public IOccurrence reference() {
        return left.reference();
    }

    @Override public IScope scope() {
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
