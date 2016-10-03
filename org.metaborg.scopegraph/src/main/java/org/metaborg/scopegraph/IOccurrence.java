package org.metaborg.scopegraph;

import java.io.Serializable;

public interface IOccurrence extends Serializable {

    boolean matches(IOccurrence other);

}