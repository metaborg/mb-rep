package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.ILabel;

public interface INamedStep extends IStep, IScopePath {

    ILabel label();

    IFullPath path();

}
