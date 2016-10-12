package org.metaborg.scopegraph.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.CyclicPathException;
import org.metaborg.scopegraph.path.IPathVisitor;
import org.metaborg.scopegraph.path.IRefPath;
import org.metaborg.scopegraph.path.IScopePath;
import org.metaborg.scopegraph.path.IStep;
import org.metaborg.scopegraph.path.PathException;
import org.metaborg.scopegraph.path.Paths;
import org.metaborg.scopegraph.path.RecursivePathException;
import org.metaborg.scopegraph.path.ScopeMismatchPathException;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;


public class RefScopePathConcat implements IRefPath {

    private static final long serialVersionUID = 6199966071731432759L;

    private final IRefPath left;
    private final IScopePath right;
    private final int size;

    public RefScopePathConcat(IRefPath left, IScopePath right) throws PathException {
        if (!left.targetScope().equals(right.sourceScope())) {
            throw new ScopeMismatchPathException();
        }
        if (Paths.isCyclic(left, right, left.targetScope())) {
            throw new CyclicPathException();
        }
        if (Paths.isRecursive(right, left.reference())) {
            throw new RecursivePathException();
        }
        this.left = left;
        this.right = right;
        this.size = left.size() + 1 + right.size();
    }

    @Override public IOccurrence reference() {
        return left.reference();
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
