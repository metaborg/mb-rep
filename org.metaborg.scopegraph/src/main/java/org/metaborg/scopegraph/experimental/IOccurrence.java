package org.metaborg.scopegraph.experimental;

import java.io.Serializable;

public interface IOccurrence extends Serializable {

    boolean matches(IOccurrence other);

}