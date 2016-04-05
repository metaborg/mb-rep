package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.IRef;

public class Ref implements IRef {
    private final String name;


    public Ref(String name) {
        this.name = name;
    }


    @Override public String name() {
        return name;
    }
}
