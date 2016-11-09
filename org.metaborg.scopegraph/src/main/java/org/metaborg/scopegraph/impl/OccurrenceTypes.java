package org.metaborg.scopegraph.impl;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.google.common.collect.Maps;

public class OccurrenceTypes implements Serializable {

    private static final ILogger logger = LoggerUtils.logger(OccurrenceTypes.class);

    private static final long serialVersionUID = 8399644577154247110L;

    private static final String CONSTRUCTOR = "Properties";
    private static final int ARITY = 1;

    private final Map<IStrategoTerm,IStrategoTerm> data;

    private OccurrenceTypes() {
        this.data = Maps.newHashMap();
    }

    public @Nullable IStrategoTerm get(IStrategoTerm occurrence) {
        return data.get(occurrence);
    }

    public Set<Entry<IStrategoTerm,IStrategoTerm>> get() {
        return data.entrySet();
    }

    public static boolean is(IStrategoTerm term) {
        return Tools.isTermAppl(term) && Tools.hasConstructor((IStrategoAppl) term, CONSTRUCTOR, ARITY);
    }

    public static OccurrenceTypes of(IStrategoTerm term) {
        if (!is(term)) {
            logger.warn("Illegal format for OccurrenceTypes: {}", Tools.constructorName(term));
            throw new IllegalArgumentException();
        }
        OccurrenceTypes occurrenceTypes = new OccurrenceTypes();
        for (IStrategoTerm occurrenceEntry : term.getSubterm(0)) {
            if (!Tools.isTermTuple(occurrenceEntry) || ((IStrategoTuple) occurrenceEntry).size() != 2) {
                logger.warn("Skipping invalid entry: ", occurrenceEntry);
                throw new IllegalArgumentException();
            }
            for (IStrategoTerm entry : occurrenceEntry.getSubterm(1)) {
                if (!Tools.isTermTuple(entry) || ((IStrategoTuple) entry).size() != 2) {
                    logger.warn("Skipping invalid subentry: ", entry);
                    throw new IllegalArgumentException();
                }
                if (Tools.isTermAppl(entry.getSubterm(0))
                        && Tools.hasConstructor((IStrategoAppl) entry.getSubterm(0), "Type", 0)) {
                    occurrenceTypes.data.put(occurrenceEntry.getSubterm(0), entry.getSubterm(1).getSubterm(0));
                    break;
                }
            }
        }
        return occurrenceTypes;
    }

}