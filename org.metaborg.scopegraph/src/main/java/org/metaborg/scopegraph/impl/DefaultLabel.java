package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.Label;

import lombok.Value;

@Value
public class DefaultLabel implements Label {
    String value;

    @Override
    public String toString() {
        return value;
    }

}
