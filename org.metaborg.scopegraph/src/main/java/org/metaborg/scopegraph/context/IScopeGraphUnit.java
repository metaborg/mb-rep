package org.metaborg.scopegraph.context;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.IScopeGraph;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraphUnit extends Serializable {

    String resource();

    @Nullable IStrategoTerm partialAnalysis();

    @Nullable IScopeGraph scopeGraph();

    @Nullable INameResolution nameResolution();

    @Nullable IStrategoTerm analysis();

}