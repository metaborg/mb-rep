package org.spoofax.interpreter.library.index;

import java.io.IOException;
import java.util.Collection;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;

public class IndexFactory {
    private final ITermFactory termFactory;
    private final IndexEntryFactory entryFactory;

    
    public IndexFactory(ITermFactory termFactory, IndexEntryFactory entryFactory) {
        this.termFactory = termFactory;
        this.entryFactory = entryFactory;
    }

    
    /**
     * Creates a term representation of given index.
     *
     * @param index
     *            The index to create a term representation of.
     * @param termFactory
     *            A term factory.
     *
     * @return A term representing given index.
     */
    public IStrategoTerm indexToTerm(IIndex index) {
        IStrategoList sourcesTerm = termFactory.makeList();
        for(IStrategoTerm source : index.getAllSources()) {
            final IStrategoTerm sourceTerm = sourceToTerm(index, source);
            sourcesTerm = termFactory.makeListCons(sourceTerm, sourcesTerm);
        }

        return sourcesTerm;
    }

    private IStrategoTerm sourceToTerm(IIndex index, IStrategoTerm source) {
        IStrategoList entryList = termFactory.makeList();
        for(IndexEntry entry : index.getInSource(source)) {
            entryList = termFactory.makeListCons(entryFactory.toTerm(entry), entryList);
        }
        return termFactory.makeTuple(source, entryList);
    }

    /**
     * Populates an index from a term representation of an index created with
     * {@link #indexToTerm(IIndex, ITermFactory, boolean)}.
     *
     * @param index
     *            The index to populate.
     * @param term
     *            A term representation of an index.
     * @throws Exception
     */
    public IIndex indexFromTerm(IIndex index, IStrategoTerm term) throws IOException {
        if(!Tools.isTermList(term)) {
            throw new IOException("Cannot read index; root term is not a list");
        }

        for(IStrategoTerm sourceTerm : term) {
            sourceFromTerm(index, sourceTerm);
        }

        return index;
    }

    private void sourceFromTerm(IIndex index, IStrategoTerm term) throws IOException {
        if(!Tools.isTermTuple(term)) {
            throw new IOException("Cannot read index; partition term is not a tuple");
        }

        final IStrategoTerm source = term.getSubterm(0);
        final IStrategoTerm entryList = term.getSubterm(1);
        final Collection<IndexEntry> entries = Lists.newArrayListWithCapacity(entryList.getSubtermCount());
        for(IStrategoTerm entryTerm : term.getSubterm(1)) {
            entries.add(entryFactory.fromTerm(entryTerm));
        }
        index.addAll(source, entries);
    }
}
