package org.metaborg.scopegraph.experimental.path.concat;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.DeclPath;
import org.metaborg.scopegraph.experimental.path.FullPath;
import org.metaborg.scopegraph.experimental.path.OccurrenceMismatchPathException;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.RecursivePathException;
import org.metaborg.scopegraph.experimental.path.RefPath;
import org.metaborg.scopegraph.experimental.path.ScopeMismatchPathException;
import org.pcollections.PSet;


public class RefDeclPathConcat implements FullPath {

    private static final long serialVersionUID = 8338879018239228235L;

    private final RefPath left;
    private final DeclPath right;
    private final int size;

    public RefDeclPathConcat(RefPath left, DeclPath right) throws PathException {
        if (!left.scope().equals(right.scope())) {
            throw new ScopeMismatchPathException();
        }
        if (!left.reference().matches(right.declaration())) {
            throw new OccurrenceMismatchPathException();
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