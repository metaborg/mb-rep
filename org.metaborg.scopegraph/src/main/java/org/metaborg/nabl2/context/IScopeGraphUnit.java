package org.metaborg.nabl2.context;

import javax.annotation.Nullable;

import org.metaborg.nabl2.solution.INameResolution;
import org.metaborg.nabl2.solution.IScopeGraph;
import org.metaborg.solver.constraints.IConstraint;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraphUnit {

    String source();

    void setMetadata(int nodeId, IStrategoTerm key, IStrategoTerm value);

    @Nullable IStrategoTerm metadata(int nodeId, IStrategoTerm key);

    @Nullable IScopeGraph scopeGraph();

    @Nullable INameResolution nameResolution();

    @Nullable IConstraint constraint();

    @Nullable IStrategoTerm analysis();

}
