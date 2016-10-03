package org.metaborg.solver;

import org.metaborg.fastutil.persistent.ObjectPSet;
import org.metaborg.unification.ITermUnifier;

public interface ISolution {

    ITermUnifier getUnifier();

    ISolution setUnifier(ITermUnifier unifier);

    ObjectPSet<String> getErrors();

    ISolution setErrors(ObjectPSet<String> errors);

}