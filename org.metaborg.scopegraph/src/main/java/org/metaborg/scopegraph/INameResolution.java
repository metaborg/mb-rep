package org.metaborg.scopegraph;

import java.util.Collection;

import org.metaborg.scopegraph.indices.ITermIndex;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface INameResolution {

    Collection<IPath> pathsFrom(IOccurrence referene);

    Collection<IStrategoTerm> astPaths(ITermIndex index);

    IStrategoTerm strategoTerm();
}