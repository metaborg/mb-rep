package org.metaborg.unification;

import org.metaborg.unification.terms.IApplTerm;
import org.metaborg.unification.terms.IConsTerm;
import org.metaborg.unification.terms.INilTerm;
import org.metaborg.unification.terms.ITermOp;
import org.metaborg.unification.terms.ITermVar;
import org.metaborg.unification.terms.ITupleTerm;

public interface ITermFunction<T> {

    T apply(ITermVar term);

    T apply(ITermOp termOp);

    T apply(IApplTerm term);

    T apply(ITupleTerm term);

    T apply(IPrimitiveTerm term);

    T apply(IConsTerm term);

    T apply(INilTerm term);

}