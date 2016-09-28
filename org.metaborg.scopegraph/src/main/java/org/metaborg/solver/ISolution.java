package org.metaborg.solver;

import org.metaborg.fastutil.persistent.PersistentObjectSet;
import org.metaborg.unification.persistent.PersistentTermUnifier;

public interface ISolution {

    PersistentTermUnifier getUnifier();

    ISolution setUnifier(PersistentTermUnifier unifier);

    PersistentObjectSet<String> getErrors();

    ISolution setErrors(PersistentObjectSet<String> errors);

}