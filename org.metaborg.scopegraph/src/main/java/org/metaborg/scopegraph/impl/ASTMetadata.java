package org.metaborg.scopegraph.impl;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nullable;

import org.metaborg.scopegraph.indices.ITermIndex;
import org.metaborg.scopegraph.indices.TermIndex;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.google.common.collect.Maps;

public class ASTMetadata implements Serializable {

    private static final ILogger logger = LoggerUtils.logger(ASTMetadata.class);

    private static final long serialVersionUID = -2013037214108032844L;

    private static final String CONSTRUCTOR = "AstMetadata";
    private static final int ARITY = 1;

    private final Map<IStrategoTerm,Map<ITermIndex,IStrategoTerm>> data;

    private ASTMetadata() {
        this.data = Maps.newHashMap();
    }

    public @Nullable IStrategoTerm get(ITermIndex key, IStrategoTerm property) {
        return get(property).get(key);
    }

    private Map<ITermIndex,IStrategoTerm> get(IStrategoTerm property) {
        Map<ITermIndex,IStrategoTerm> properties;
        if ((properties = data.get(property)) == null) {
            data.put(property, (properties = Maps.newHashMap()));
        }
        return properties;
    }

    public static boolean is(IStrategoTerm term) {
        return Tools.isTermAppl(term) && Tools.hasConstructor((IStrategoAppl) term, CONSTRUCTOR, ARITY);
    }

    public static ASTMetadata of(IStrategoTerm term) {
        if (!is(term)) {
            logger.warn("Illegal format for ASTMetadata: {}", Tools.constructorName(term));
            throw new IllegalArgumentException();
        }
        ASTMetadata astMetadata = new ASTMetadata();
        for (IStrategoTerm termEntry : term.getSubterm(0)) {
            TermIndex index;
            if (!Tools.isTermTuple(termEntry) || ((IStrategoTuple) termEntry).size() != 2
                    || (index = TermIndex.TYPE.fromTerm((IStrategoAppl) termEntry.getSubterm(0))) == null) {
                logger.warn("Skipping invalid entry: ", termEntry);
                throw new IllegalArgumentException();
            }
            for (IStrategoTerm entry : termEntry.getSubterm(1)) {
                if (!Tools.isTermTuple(entry) || ((IStrategoTuple) entry).size() != 2) {
                    logger.warn("Skipping invalid subentry: ", entry);
                    throw new IllegalArgumentException();
                }
                astMetadata.get(entry.getSubterm(0)).put(index, entry.getSubterm(1));
            }
        }
        return astMetadata;
    }

}