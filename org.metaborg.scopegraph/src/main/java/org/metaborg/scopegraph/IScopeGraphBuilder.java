package org.metaborg.scopegraph;

import com.google.common.collect.ClassToInstanceMap;

public interface IScopeGraphBuilder {
    IScope scope(int id);

    IDecl decl(String name);

    IDecl decl(String name, IScope assoc);

    IDecl decl(String name, ClassToInstanceMap<Object> data);

    IDecl decl(String name, IScope assoc, ClassToInstanceMap<Object> data);

    IRef ref(String name);


    void edge(IDecl decl, IScope scope);

    void edge(IScope scope, IRef ref);

    void edge(IScope src, IScopeLabel label, IScope dst);

    void edge(IScope src, IRefLabel label, IRef dst);


    IScopeGraph build(IScope root);
}
