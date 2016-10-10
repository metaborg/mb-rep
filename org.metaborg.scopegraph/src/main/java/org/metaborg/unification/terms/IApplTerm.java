package org.metaborg.unification.terms;

public interface IApplTerm extends ITermWithArgs {

    String getOp();

    int getArity();

}