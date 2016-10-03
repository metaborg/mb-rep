package org.metaborg.scopegraph.experimental.path;


public interface IPathVisitor<T> {

    T visit(IRefPath path) throws PathException;

    T visit(IScopePath path) throws PathException;

    T visit(IDeclPath path) throws PathException;

    T visit(IFullPath path) throws PathException;

}
