package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.IOccurrence;

public interface IFullPath extends IPath {

    IOccurrence reference();

    IOccurrence declaration();

}
