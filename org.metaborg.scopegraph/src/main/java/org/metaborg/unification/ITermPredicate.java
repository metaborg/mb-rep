package org.metaborg.unification;

import org.metaborg.unification.terms.IApplTerm;
import org.metaborg.unification.terms.IConsTerm;
import org.metaborg.unification.terms.INilTerm;
import org.metaborg.unification.terms.ITermOp;
import org.metaborg.unification.terms.ITermVar;
import org.metaborg.unification.terms.ITupleTerm;

public interface ITermPredicate {

    boolean test(ITermVar term);

    boolean test(ITermOp termOp);

    boolean test(IApplTerm term);

    boolean test(ITupleTerm term);

    boolean test(IPrimitiveTerm term);

    boolean test(IConsTerm term);

    boolean test(INilTerm term);

}