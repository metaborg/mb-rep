package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.ILabel;

public interface INamedStep extends IStep, IScopePath {

    ILabel label();

    IFullPath path();

}
