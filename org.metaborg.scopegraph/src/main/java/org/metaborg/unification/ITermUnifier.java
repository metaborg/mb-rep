package org.metaborg.unification;

import java.util.Set;

import org.metaborg.unification.terms.TermVar;


public interface ITermUnifier {

    IUnifyResult unify(ITerm term1, ITerm term2);

    IFindResult find(ITerm term);

    ITermUnifier findAll();

    Set<TermVar> variables();

}