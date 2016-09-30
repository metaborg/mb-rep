package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.IOccurrence;
import org.metaborg.scopegraph.experimental.IScope;

public interface RefPath extends IPath {

    IOccurrence reference();

    IScope scope();

}
