package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.IScope;

public interface IScopePath extends IPath {

    IScope sourceScope();

    IScope targetScope();
}
