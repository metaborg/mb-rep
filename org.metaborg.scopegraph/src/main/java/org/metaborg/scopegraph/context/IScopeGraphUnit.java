package org.metaborg.scopegraph.context;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.IScopeGraph;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface IScopeGraphUnit {

    String source();

    void setMetadata(int nodeId, IStrategoTerm key, IStrategoTerm value);
    @Nullable IStrategoTerm metadata(int nodeId, IStrategoTerm key);
 
    @Nullable IScopeGraph scopeGraph();
    @Nullable INameResolution nameResolution();
    @Nullable IStrategoTerm analysis();

}
