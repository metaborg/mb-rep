package org.metaborg.scopegraph;

import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraph {

    Collection<IScope> scopes();
 
    IStrategoTerm strategoTerm();
 
}
