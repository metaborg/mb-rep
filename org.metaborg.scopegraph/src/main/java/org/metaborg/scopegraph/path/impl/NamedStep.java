package org.metaborg.scopegraph.path.impl;

import org.metaborg.scopegraph.IRef;
import org.metaborg.scopegraph.IRefLabel;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.path.INamedStep;

public class NamedStep implements INamedStep {
    private final IRefLabel label;
    private final IRef ref;
    private final IScope scope;


    public NamedStep(IRefLabel label, IRef ref, IScope scope) {
        this.label = label;
        this.ref = ref;
        this.scope = scope;
    }


    @Override public IRefLabel label() {
        return label;
    }

    @Override public IRef ref() {
        return ref;
    }

    @Override public IScope scope() {
        return scope;
    }
}
