package org.metaborg.scopegraph;

import java.io.Serializable;

public interface ILabeledOccurrence extends Serializable {

    ILabel label();

    IOccurrence occurrence();

}
