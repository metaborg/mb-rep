package org.metaborg.nabl2.solution;

import java.util.Collection;

import org.metaborg.nabl2.indices.ITermIndex;
import org.spoofax.interpreter.terms.IStrategoTerm;

public interface INameResolution {

    Collection<IPath> pathsFrom(IOccurrence referene);

    Collection<IStrategoTerm> astPaths(ITermIndex index);

    IStrategoTerm strategoTerm();
}