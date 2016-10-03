package org.metaborg.scopegraph;

import java.io.Serializable;

public interface ILabeledScope extends Serializable {

    ILabel label();

    IScope scope();

}
