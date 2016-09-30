package org.metaborg.scopegraph.experimental.path.step;

import org.metaborg.scopegraph.experimental.ILabel;
import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;
import org.metaborg.scopegraph.experimental.path.CyclicPathException;
import org.metaborg.scopegraph.experimental.path.PathException;
import org.metaborg.scopegraph.experimental.path.ScopePath;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;


public final class DirectStep implements ScopePath {

    private static final long serialVersionUID = 8232068669592543089L;

    private final IScope sourceScope;
    private final ILabel label;
    private final IScope targetScope;

    public DirectStep(IScope sourceScope, ILabel label, IScope targetScope) throws PathException {
        if (sourceScope.equals(targetScope)) {
            throw new CyclicPathException();
        }
        this.sourceScope = sourceScope;
        this.label = label;
        this.targetScope = targetScope;
    }

    @Override public IScope sourceScope() {
        return sourceScope;
    }

    public ILabel label() {
        return label;
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
        return HashTreePSet.empty();
    }

}
