package org.metaborg.scopegraph.impl;

import org.metaborg.scopegraph.IEdge;

public class Edge<L, T> implements IEdge<L, T> {
    private final L label;
    private final T dest;


    public Edge(L label, T dest) {
        this.label = label;
        this.dest = dest;
    }


    @Override public L label() {
        return label;
    }

    @Override public T dest() {
        return dest;
    }
}
