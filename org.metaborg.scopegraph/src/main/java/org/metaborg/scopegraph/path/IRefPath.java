package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IScope;

public interface IRefPath extends IPath {

    IOccurrence reference();

    IScope targetScope();
}
