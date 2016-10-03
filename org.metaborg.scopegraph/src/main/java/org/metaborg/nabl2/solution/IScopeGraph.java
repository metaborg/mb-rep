package org.metaborg.nabl2.solution;

import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraph {

    Collection<IScope> scopes();

    IStrategoTerm strategoTerm();

}