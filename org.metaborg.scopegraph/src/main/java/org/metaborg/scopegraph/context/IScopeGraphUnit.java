package org.metaborg.scopegraph.context;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.IScopeGraph;
import org.metaborg.scopegraph.impl.ASTMetadata;
import org.metaborg.scopegraph.impl.OccurrenceTypes;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraphUnit extends Serializable {

    String resource();

    @Nullable IStrategoTerm partialAnalysis();

    @Nullable IScopeGraph scopeGraph();

    @Nullable INameResolution nameResolution();

    @Nullable ASTMetadata astMetadata();

    @Nullable OccurrenceTypes occurrenceTypes();

    @Nullable IStrategoTerm analysis();

}