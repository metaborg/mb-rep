package org.metaborg.scopegraph;

import javax.annotation.Nullable;

public interface IScope {
    int id();


    Iterable<? extends IDecl> decls();

    Iterable<? extends IRef> refs();


    @Nullable IEdge<IScopeLabel, IScope> directEdge();

    Iterable<? extends IEdge<IRefLabel, IRef>> namedEdges();
}
