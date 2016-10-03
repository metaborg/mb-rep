package org.metaborg.scopegraph.experimental.path.free;

import java.util.Iterator;

import org.metaborg.scopegraph.experimental.ILabel;
import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.IFullPath;
import org.metaborg.scopegraph.experimental.path.INamedStep;
import org.metaborg.scopegraph.experimental.path.IPathVisitor;
import org.metaborg.scopegraph.experimental.path.IStep;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import com.google.common.collect.Iterators;

public final class NamedStep implements INamedStep {

    private static final long serialVersionUID = 2375221392501994432L;

    private final IScope sourceScope;
    private final ILabel label;
    private final IFullPath path;
    private final IScope targetScope;

    public NamedStep(IScope sourceScope, ILabel label, IFullPath path, IScope targetScope) throws PathException {
        if (sourceScope.equals(targetScope)) {
            throw new CyclicPathException();
        }
        this.sourceScope = sourceScope;
        this.label = label;
        this.path = path;
        this.targetScope = targetScope;
    }

    @Override public IScope sourceScope() {
        return sourceScope;
    }

    @Override public ILabel label() {
        return label;
    }

    @Override public IFullPath path() {
        return path;
    }

    @Override public IScope targetScope() {
        return targetScope;
    }

    @Override public int size() {
        return 1;
    }

    @Override public PSet<IScope> scopes() {
        return HashTreePSet.singleton(sourceScope).plus(targetScope);
    }

    @Override public PSet<IOccurrence> references() {
        return path.references();
    }

    @Override public <T> T accept(IPathVisitor<T> visitor) throws PathException {
        return visitor.visit(this);
    }

    @Override public Iterator<IStep> iterator() {
        return Iterators.<IStep> singletonIterator(this);
    }

}
