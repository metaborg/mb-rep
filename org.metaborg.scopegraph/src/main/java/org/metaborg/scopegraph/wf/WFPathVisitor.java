package org.metaborg.scopegraph.wf;

import org.metaborg.scopegraph.path.PathException;

public interface WFPathVisitor<T> {

    T visit(WFDeclPath path) throws PathException;

    T visit(WFFullPath path) throws PathException;

}
