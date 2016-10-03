package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IScope;

public interface IScopePath extends IPath {

    IScope sourceScope();

    IScope targetScope();
}
