package org.metaborg.scopegraph;

public interface IEdge<L, T> {
    L label();

    T dest();
}
