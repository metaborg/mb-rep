package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IRef;
import org.metaborg.scopegraph.IRefLabel;
import org.metaborg.scopegraph.IScope;

public interface INamedStep extends IStep {
    IRefLabel label();

    IRef ref();

    IScope scope();
}
