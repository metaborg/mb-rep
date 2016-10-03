package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;

public interface IDeclPath extends IPath {

    IScope sourceScope();

    IOccurrence declaration();

}