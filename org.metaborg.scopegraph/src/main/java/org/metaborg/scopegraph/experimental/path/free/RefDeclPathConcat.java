package org.metaborg.scopegraph.experimental.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.IDeclPath;
import org.metaborg.scopegraph.experimental.path.IFullPath;
import org.metaborg.scopegraph.experimental.path.IPathVisitor;
import org.metaborg.scopegraph.experimental.path.IRefPath;
import org.metaborg.scopegraph.experimental.path.IStep;
import org.metaborg.scopegraph.experimental.path.OccurrenceMismatchPathException;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.Paths;
import org.metaborg.scopegraph.experimental.path.RecursivePathException;
import org.metaborg.scopegraph.experimental.path.ScopeMismatchPathException;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;


public class RefDeclPathConcat implements IFullPath {

    private static final long serialVersionUID = 8338879018239228235L;

    private final IRefPath left;
    private final IDeclPath right;
    private final int size;

    public RefDeclPathConcat(IRefPath left, IDeclPath right) throws PathException {
        if (!left.targetScope().equals(right.sourceScope())) {
            throw new ScopeMismatchPathException();
        }
        if (!left.reference().matches(right.declaration())) {
            throw new OccurrenceMismatchPathException();
        }
        if (Paths.cyclic(left, right, left.targetScope())) {
            throw new CyclicPathException();
        }
        if (Paths.recursive(right, left.reference())) {
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

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit(this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators.concat(left.iterator(), right.iterator());
    }

}