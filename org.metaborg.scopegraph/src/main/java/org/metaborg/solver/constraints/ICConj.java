package org.metaborg.solver.constraints;

import com.google.common.collect.ImmutableList;

public interface ICConj extends IConstraint {

    ImmutableList<IConstraint> getConstraints();

}