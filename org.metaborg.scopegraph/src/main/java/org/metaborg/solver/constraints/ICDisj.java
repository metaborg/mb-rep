package org.metaborg.solver.constraints;

import com.google.common.collect.ImmutableList;

public interface ICDisj extends IConstraint {

    ImmutableList<IConstraint> getConstraints();

}