package org.metaborg.scopegraph.experimental.path.concat;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.DeclPath;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.ScopeMismatchPathException;
import org.metaborg.scopegraph.experimental.path.ScopePath;
import org.pcollections.PSet;


public class ScopeDeclPathConcat implements DeclPath {

    private static final long serialVersionUID = 2559587007188951091L;

    private final ScopePath left;
    private final DeclPath right;
    private final int size;

    public ScopeDeclPathConcat(ScopePath left, DeclPath right) throws PathException {
        if (!left.targetScope().equals(right.scope())) {
            throw new ScopeMismatchPathException();
        }
        if (PathConcat.cyclic(left, right, left.targetScope())) {
            throw new CyclicPathException();
        }
        this.left = left;
        this.right = right;
        this.size = left.size() + 1 + right.size();
    }

    @Override public IScope scope() {
        return left.sourceScope();
    }

    @Override public IOccurrence declaration() {
        return right.declaration();
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
