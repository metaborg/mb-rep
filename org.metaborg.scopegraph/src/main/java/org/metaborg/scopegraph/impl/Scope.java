package org.metaborg.scopegraph.impl;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.IDecl;
import org.metaborg.scopegraph.IEdge;
import org.metaborg.scopegraph.IRef;
import org.metaborg.scopegraph.IRefLabel;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeLabel;

public class Scope implements IScope {
    private final int id;
    private final Iterable<? extends IDecl> decls;
    private final Iterable<? extends IRef> refs;
    private final @Nullable IEdge<IScopeLabel, IScope> directEdge;
    private final Iterable<? extends IEdge<IRefLabel, IRef>> namedEdges;


    public Scope(int id, Iterable<? extends IDecl> decls, Iterable<? extends IRef> refs,
        IEdge<IScopeLabel, IScope> directEdge, Iterable<? extends IEdge<IRefLabel, IRef>> namedEdges) {
        this.id = id;
        this.decls = decls;
        this.refs = refs;
        this.directEdge = directEdge;
        this.namedEdges = namedEdges;
    }


    @Override public int id() {
        return id;
    }

    @Override public Iterable<? extends IDecl> decls() {
        return decls;
    }

    @Override public Iterable<? extends IRef> refs() {
        return refs;
    }

    @Override public @Nullable IEdge<IScopeLabel, IScope> directEdge() {
        return directEdge;
    }

    @Override public Iterable<? extends IEdge<IRefLabel, IRef>> namedEdges() {
        return namedEdges;
    }
}
