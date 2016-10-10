package org.metaborg.solver.constraints;

import org.metaborg.unification.ITerm;

public interface ICInequal extends IConstraint {

    ITerm getFirst();

    ITerm getSecond();

}
