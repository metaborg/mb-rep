package org.metaborg.unification;

import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.NilTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;
import org.metaborg.unification.terms.TupleTerm;

public interface ITermFunction<T> {

    T apply(TermVar term);

    T apply(TermOp termOp);

    T apply(ApplTerm term);

    T apply(TupleTerm term);

    T apply(IPrimitiveTerm term);

    T apply(ConsTerm term);

    T apply(NilTerm term);

}