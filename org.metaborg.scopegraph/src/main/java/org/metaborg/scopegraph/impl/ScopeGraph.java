package org.metaborg.scopegraph.impl;

import java.io.Serializable;
import java.util.Collection;

import org.metaborg.scopegraph.IScope;
import org.metaborg.scopegraph.IScopeGraph;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class ScopeGraph implements IScopeGraph, Serializable {

    private static final ILogger logger = LoggerUtils.logger(ScopeGraph.class);

    private static final long serialVersionUID = 1470444925583742762L;

    private static final String CONSTRUCTOR = "ScopeGraph";
    private static final int ARITY = 1;

    private final IStrategoTerm term;

    private ScopeGraph(IStrategoTerm term) {
        this.term = term;
    }

    @Override public Collection<IScope> scopes() {
        return null;
    }

    public IStrategoTerm strategoTerm() {
        return term;
    }

    public static boolean is(IStrategoTerm term) {
        return Tools.isTermAppl(term) && Tools.hasConstructor((IStrategoAppl) term, CONSTRUCTOR, ARITY);
    }

    public static ScopeGraph of(IStrategoTerm term) {
        if (!is(term)) {
            logger.warn("Illegal format for ScopeGraph: {}", term);
            throw new IllegalArgumentException();
        }
        return new ScopeGraph(term.getSubterm(0));
    }

}