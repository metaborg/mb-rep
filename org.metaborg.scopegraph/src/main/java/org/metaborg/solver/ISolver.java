package org.metaborg.solver;

import org.metaborg.unification.persistent.TermUnifier;

public interface ISolver {

    ISolver add(IConstraint constraint);
    ISolver addAll(Iterable<IConstraint> constraint);

    TermUnifier unifier();
    ISolver setUnifier(TermUnifier unifier);
 
}