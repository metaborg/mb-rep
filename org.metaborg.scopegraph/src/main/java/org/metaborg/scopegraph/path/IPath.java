package org.metaborg.scopegraph.path;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.IDecl;

public interface IPath {
    Iterable<IStep> steps();

    boolean complete();

    @Nullable IDecl decl();
}
