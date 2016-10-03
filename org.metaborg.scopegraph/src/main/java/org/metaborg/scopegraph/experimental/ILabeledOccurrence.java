package org.metaborg.scopegraph.experimental;

import java.io.Serializable;

public interface ILabeledOccurrence extends Serializable {

    ILabel label();

    IOccurrence occurrence();

}
