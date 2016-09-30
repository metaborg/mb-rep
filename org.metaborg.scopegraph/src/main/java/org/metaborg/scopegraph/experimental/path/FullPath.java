package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.IOccurrence;

public interface FullPath extends IPath {

    IOccurrence reference();

    IOccurrence declaration();

}
