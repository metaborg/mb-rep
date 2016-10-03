package org.metaborg.scopegraph.experimental.path;

import org.metaborg.scopegraph.experimental.ILabel;

public interface IDirectStep extends IStep, IScopePath {

    ILabel label();

}