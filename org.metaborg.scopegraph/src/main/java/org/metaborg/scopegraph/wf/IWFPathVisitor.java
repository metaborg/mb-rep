package org.metaborg.scopegraph.wf;

import org.metaborg.scopegraph.path.PathException;

public interface IWFPathVisitor<T> {

    T visit(IWFDeclPath path) throws PathException;

    T visit(IWFFullPath path) throws PathException;

}
