package org.metaborg.scopegraph.path.impl;

import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeLabel;
import org.metaborg.scopegraph.path.IDirectStep;

public class DirectStep implements IDirectStep {
    private final IScopeLabel label;
    private final IScope scope;


    public DirectStep(IScopeLabel label, IScope scope) {
        this.label = label;
        this.scope = scope;
    }


    @Override public IScopeLabel label() {
        return label;
    }

    @Override public IScope scope() {
        return scope;
    }
}
