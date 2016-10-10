package org.metaborg.unification.terms;

import org.metaborg.unification.ITerm;

import com.google.common.collect.ImmutableList;

public interface ITermWithArgs extends ITerm {

    ImmutableList<ITerm> getArgs();

}
