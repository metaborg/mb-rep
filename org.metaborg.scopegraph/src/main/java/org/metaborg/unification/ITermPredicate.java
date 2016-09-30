package org.metaborg.unification;

import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.NilTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;
import org.metaborg.unification.terms.TupleTerm;

public interface ITermPredicate {

    boolean test(TermVar term);

    boolean test(TermOp termOp);

    boolean test(ApplTerm term);

    boolean test(TupleTerm term);

    boolean test(IPrimitiveTerm term);

    boolean test(ConsTerm term);

    boolean test(NilTerm term);

}