package org.metaborg.scopegraph;

public interface IScopeGraph {
    IScope root();

    Iterable<? extends IScope> scopes();
}
