package org.metaborg.scopegraph.impl;

import java.io.Serializable;
import java.util.Collection;

import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeGraph;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class ScopeGraph implements IScopeGraph, Serializable {

    private static final long serialVersionUID = 1470444925583742762L;

    public ScopeGraph(IStrategoTerm term) {
    }
    
    @Override
    public Collection<IScope> scopes() {
        return null;
    }

}
