package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeGraph;

import com.google.common.collect.Lists;

public class ScopeGraph implements IScopeGraph {
    private final IScope root;
    private final Iterable<? extends IScope> scopes;


    public ScopeGraph(IScope root) {
        this(root, Lists.newArrayList(root));
    }

    public ScopeGraph(IScope root, Iterable<? extends IScope> scopes) {
        this.root = root;
        this.scopes = scopes;
    }


    @Override public IScope root() {
        return root;
    }

    @Override public Iterable<? extends IScope> scopes() {
        return scopes;
    }
}
