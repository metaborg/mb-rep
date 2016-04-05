package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeGraph;
import org.metaborg.scopegraph.IScopeGraphBuilder;
import org.metaborg.scopegraph.IScopeGraphFactory;

import com.google.common.collect.Lists;

public class ScopeGraphFactory implements IScopeGraphFactory {
    @Override public IScopeGraph of(IScope root) {
        return new ScopeGraph(root);
    }

    @Override public IScopeGraph of(IScope root, Iterable<IScope> scopes) {
        return new ScopeGraph(root, scopes);
    }

    @Override public IScopeGraph of(IScope root, IScope... scopes) {
        return new ScopeGraph(root, Lists.newArrayList(scopes));
    }


    @Override public IScopeGraphBuilder builder() {
        return new ScopeGraphBuilder();
    }
}
