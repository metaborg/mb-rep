package org.metaborg.scopegraph.experimental;

import java.io.Serializable;

public interface ILabeledScope extends Serializable {

    ILabel label();

    IScope scope();

}
