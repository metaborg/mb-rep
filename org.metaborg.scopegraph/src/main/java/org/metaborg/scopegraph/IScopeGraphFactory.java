package org.metaborg.scopegraph;

public interface IScopeGraphFactory {
    IScopeGraph of(IScope root);

    IScopeGraph of(IScope root, Iterable<IScope> scopes);

    IScopeGraph of(IScope root, IScope... scopes);


    IScopeGraphBuilder builder();
}
