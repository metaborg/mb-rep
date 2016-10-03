package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.ILabel;

public interface IDirectStep extends IStep, IScopePath {

    ILabel label();

}