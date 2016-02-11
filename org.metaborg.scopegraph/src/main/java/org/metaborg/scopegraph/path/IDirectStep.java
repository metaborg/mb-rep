package org.metaborg.scopegraph.path;

import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeLabel;

public interface IDirectStep extends IStep {
    IScopeLabel label();

    IScope scope();
}
