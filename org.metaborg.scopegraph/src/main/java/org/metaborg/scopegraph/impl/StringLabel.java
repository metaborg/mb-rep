package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.IStringLabel;

public class StringLabel implements IStringLabel {
    private final String id;


    public StringLabel(String id) {
        this.id = id;
    }


    @Override public String id() {
        return id;
    }
}
