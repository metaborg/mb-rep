package org.metaborg.scopegraph.impl;

import java.util.Collection;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.IDecl;
import org.metaborg.scopegraph.IEdge;
import org.metaborg.scopegraph.IRef;
import org.metaborg.scopegraph.IRefLabel;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeLabel;

import com.google.common.collect.Lists;

public class MutableScope implements IScope {
    private final int id;
    private final Collection<IDecl> decls = Lists.newArrayList();
    private final Collection<IRef> refs = Lists.newArrayList();
    private final Collection<IEdge<IRefLabel, IRef>> namedEdges = Lists.newArrayList();

    private @Nullable IEdge<IScopeLabel, IScope> directEdge;


    public MutableScope(int id) {
        this.id = id;
    }


    @Override public int id() {
        return id;
    }

    @Override public Iterable<IDecl> decls() {
        return decls;
    }

    @Override public Iterable<IRef> refs() {
        return refs;
    }

    @Override public @Nullable IEdge<IScopeLabel, IScope> directEdge() {
        return directEdge;
    }

    @Override public Iterable<IEdge<IRefLabel, IRef>> namedEdges() {
        return namedEdges;
    }


    public void addDecl(IDecl decl) {
        decls.add(decl);
    }

    public void addRef(IRef ref) {
        refs.add(ref);
    }

    public void setDirectEdge(@Nullable IEdge<IScopeLabel, IScope> edge) {
        directEdge = edge;
    }

    public void addNamedEdge(IEdge<IRefLabel, IRef> edge) {
        namedEdges.add(edge);
    }
}
