package org.metaborg.scopegraph;

import javax.annotation.Nullable;

public interface IDecl {
    String name();

    @Nullable IScope assoc();

    @Nullable <T> T data(Class<T> dataType);
}
