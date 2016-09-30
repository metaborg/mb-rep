package org.metaborg.solver;

import org.metaborg.fastutil.persistent.PersistentObjectSet;
import org.metaborg.unification.ITermUnifier;

public interface ISolution {

    ITermUnifier getUnifier();

    ISolution setUnifier(ITermUnifier unifier);

    PersistentObjectSet<String> getErrors();

    ISolution setErrors(PersistentObjectSet<String> errors);

}