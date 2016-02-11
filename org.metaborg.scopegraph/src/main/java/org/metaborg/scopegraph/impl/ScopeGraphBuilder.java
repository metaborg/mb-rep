package org.metaborg.scopegraph.impl;

import java.util.Collection;

import org.metaborg.scopegraph.IDecl;
import org.metaborg.scopegraph.IRef;
import org.metaborg.scopegraph.IRefLabel;
import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeGraph;
import org.metaborg.scopegraph.IScopeGraphBuilder;
import org.metaborg.scopegraph.IScopeLabel;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.Lists;

public class ScopeGraphBuilder implements IScopeGraphBuilder {
    private final Collection<MutableScope> scopes = Lists.newArrayList();


    @Override public IScope scope(int id) {
        final MutableScope scope = new MutableScope(id);
        scopes.add(scope);
        return scope;
    }

    @Override public IDecl decl(String name) {
        return new Decl(name);
    }

    @Override public IDecl decl(String name, IScope assoc) {
        return new Decl(name, assoc);
    }

    @Override public IDecl decl(String name, ClassToInstanceMap<Object> data) {
        return new Decl(name, data);
    }

    @Override public IDecl decl(String name, IScope assoc, ClassToInstanceMap<Object> data) {
        return new Decl(name, assoc, data);
    }

    @Override public IRef ref(String name) {
        return new Ref(name);
    }

    @Override public void edge(IDecl decl, IScope scope) {
        final MutableScope mScope = (MutableScope) scope;
        mScope.addDecl(decl);
    }

    @Override public void edge(IScope scope, IRef ref) {
        final MutableScope mScope = (MutableScope) scope;
        mScope.addRef(ref);
    }

    @Override public void edge(IScope src, IScopeLabel label, IScope dst) {
        final MutableScope srcScope = (MutableScope) src;
        final MutableScope dstScope = (MutableScope) src;
        srcScope.setDirectEdge(new Edge<IScopeLabel, IScope>(label, dstScope));
    }

    @Override public void edge(IScope src, IRefLabel label, IRef dst) {
        final MutableScope scope = (MutableScope) src;
        scope.addNamedEdge(new Edge<IRefLabel, IRef>(label, dst));
    }

    @Override public IScopeGraph build(IScope root) {
        return new ScopeGraph(root, scopes);
    }
}
