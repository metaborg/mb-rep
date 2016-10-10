package org.metaborg.solver.constraints;

import org.metaborg.unification.ITerm;

public interface ICEqual extends IConstraint {

    ITerm getFirst();

    ITerm getSecond();

}
