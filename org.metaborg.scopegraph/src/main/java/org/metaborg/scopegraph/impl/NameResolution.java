package org.metaborg.scopegraph.impl;

import java.io.Serializable;
import java.util.Collection;

import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IPath;
import org.metaborg.scopegraph.ScopeGraphException;
import org.metaborg.scopegraph.indices.ITermIndex;
import org.metaborg.scopegraph.indices.TermIndex;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class NameResolution implements INameResolution, Serializable {

    private static final long serialVersionUID = -6131067813443915513L;

    private final IStrategoTerm term;
    private Multimap<IOccurrence,IPath> pathsFrom;
    private Multimap<ITermIndex,IStrategoTerm> astPaths;

    public NameResolution(IStrategoTerm term) throws ScopeGraphException {
        this.term = term;
        pathsFrom = HashMultimap.create();
        astPaths = HashMultimap.create();
        if (!term.isList()) {
            throw new ScopeGraphException("Name resolution is not a list: " + term);
        }
        for (IStrategoTerm resolution : term) {
            IStrategoTerm refTerm = resolution.getSubterm(0);
            IStrategoTerm resolutionTerm = resolution.getSubterm(1);
            if (Tools.isTermAppl(resolutionTerm) && Tools.hasConstructor((IStrategoAppl) resolutionTerm, "None", 0)) {
                continue;
            }
            IStrategoTerm declTerm = resolutionTerm.getSubterm(0);
            IStrategoTerm steps = resolution.getSubterm(1).getSubterm(1);
            Occurrence ref = new Occurrence(refTerm);
            Occurrence decl = new Occurrence(declTerm);
            IPath path = new Path(ref, decl, steps);
            pathsFrom.put(ref, path);
            ITermIndex index = TermIndex.TYPE.fromTerm((IStrategoAppl) ref.index());
            astPaths.put(index, decl.index());
        }
    }

    @Override public Collection<IPath> pathsFrom(IOccurrence reference) {
        return pathsFrom.get(reference);
    }

    @Override public Collection<IStrategoTerm> astPaths(ITermIndex index) {
        return astPaths.get(index);
    }

    public IStrategoTerm strategoTerm() {
        return term;
    }
}
