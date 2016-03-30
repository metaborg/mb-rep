package org.metaborg.scopegraph.impl;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.IDecl;
import org.metaborg.scopegraph.IScope;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.ImmutableClassToInstanceMap;

public class Decl implements IDecl {
    private final String name;
    private final @Nullable IScope assoc;
    private final ClassToInstanceMap<Object> data;


    public Decl(String name) {
        this(name, null, ImmutableClassToInstanceMap.builder().build());
    }

    public Decl(String name, IScope assoc) {
        this(name, assoc, ImmutableClassToInstanceMap.builder().build());
    }

    public Decl(String name, ClassToInstanceMap<Object> data) {
        this(name, null, data);
    }

    public Decl(String name, IScope assoc, ClassToInstanceMap<Object> data) {
        this.name = name;
        this.assoc = assoc;
        this.data = data;
    }


    @Override public String name() {
        return name;
    }

    @Override public @Nullable IScope assoc() {
        return assoc;
    }

    @Override public @Nullable <T> T data(Class<T> dataType) {
        return data.getInstance(dataType);
    }
}
