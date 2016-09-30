package org.metaborg.scopegraph.experimental.path.step;

import org.metaborg.scopegraph.experimental.ILabel;
import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.FullPath;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.ScopePath;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

public final class NamedStep implements ScopePath {

    private static final long serialVersionUID = 2375221392501994432L;

    private final IScope sourceScope;
    private final ILabel label;
    private final FullPath path;
    private final IScope targetScope;

    public NamedStep(IScope sourceScope, ILabel label, FullPath path, IScope targetScope) throws PathException {
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

    public ILabel label() {
        return label;
    }

    public FullPath path() {
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

}
