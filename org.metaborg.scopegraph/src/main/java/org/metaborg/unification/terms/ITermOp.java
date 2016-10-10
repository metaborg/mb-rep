package org.metaborg.unification.terms;

import org.metaborg.unification.IAny;
import org.metaborg.unification.ITerm;

import com.google.common.collect.ImmutableList;

public interface ITermOp extends IAny {

    String getOp();

    ImmutableList<ITerm> getArgs();

    int getArity();

}