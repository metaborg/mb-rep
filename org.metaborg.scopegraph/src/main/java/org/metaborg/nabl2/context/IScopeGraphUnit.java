package org.metaborg.nabl2.context;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.metaborg.nabl2.solution.INameResolution;
import org.metaborg.nabl2.solution.IScopeGraph;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraphUnit extends Serializable {

    String resource();

    @Nullable IStrategoTerm partialAnalysis();

    @Nullable IScopeGraph scopeGraph();

    @Nullable INameResolution nameResolution();

    @Nullable IStrategoTerm analysis();

}