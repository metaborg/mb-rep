package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;

public interface IDeclPath extends IPath {

    IScope sourceScope();

    IOccurrence declaration();

}