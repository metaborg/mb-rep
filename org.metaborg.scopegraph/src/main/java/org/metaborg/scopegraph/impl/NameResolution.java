package org.metaborg.scopegraph.impl;

import java.io.Serializable;
import java.util.Collection;

import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.IOccurrence;
import org.metaborg.scopegraph.IPath;
import org.metaborg.scopegraph.ScopeGraphException;
import org.metaborg.scopegraph.indices.ITermIndex;
import org.metaborg.scopegraph.indices.TermIndex;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class NameResolution implements INameResolution, Serializable {

    private static final ILogger logger = LoggerUtils.logger(NameResolution.class);

    private static final long serialVersionUID = -6131067813443915513L;

    private static final String CONSTRUCTOR = "NameResolution";
    private static final int ARITY = 1;

    private final IStrategoTerm term;
    private Multimap<IOccurrence,IPath> pathsFrom;
    private Multimap<ITermIndex,IStrategoTerm> astPaths;

    private NameResolution(IStrategoTerm term) {
        this.term = term;
        pathsFrom = HashMultimap.create();
        astPaths = HashMultimap.create();
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

    public static boolean is(IStrategoTerm term) {
        return Tools.isTermAppl(term) && Tools.hasConstructor((IStrategoAppl) term, CONSTRUCTOR, ARITY);
    }

    public static NameResolution of(IStrategoTerm term) {
        if (!is(term)) {
            logger.warn("Illegal format for NameResolution: {}", term);
            throw new IllegalArgumentException();
        }
        NameResolution nameResolution = new NameResolution(term.getSubterm(0));
        for (IStrategoTerm resolution : term.getSubterm(0)) {
            IStrategoTerm refTerm = resolution.getSubterm(0);
            IStrategoTerm resolutionTerm = resolution.getSubterm(1);
            if (Tools.isTermAppl(resolutionTerm) && Tools.hasConstructor((IStrategoAppl) resolutionTerm, "None", 0)) {
                continue;
            }
            IStrategoTerm declTerm = resolutionTerm.getSubterm(0);
            IStrategoTerm steps = resolution.getSubterm(1).getSubterm(1);
            try {
                Occurrence ref = new Occurrence(refTerm);
                Occurrence decl = new Occurrence(declTerm);
                IPath path = new Path(ref, decl, steps);
                nameResolution.pathsFrom.put(ref, path);
                ITermIndex index = TermIndex.TYPE.fromTerm((IStrategoAppl) ref.index());
                nameResolution.astPaths.put(index, decl.index());
            } catch (ScopeGraphException e) {
                logger.warn("Illegal format for NameResolution.", e);
                throw new IllegalArgumentException();
            }
        }
        return nameResolution;
    }

}