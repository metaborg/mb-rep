package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IOccurrence;

public interface IFullPath extends IPath {

    IOccurrence reference();

    IOccurrence declaration();

}
