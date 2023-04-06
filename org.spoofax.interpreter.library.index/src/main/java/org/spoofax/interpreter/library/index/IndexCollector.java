package org.spoofax.interpreter.library.index;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.metaborg.util.collection.MultiSet;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

public class IndexCollector {
    private final ITermFactory termFactory;
    private final IndexEntryFactory entryFactory;

    private MultiSet.Transient<IndexEntry> addedEntries = MultiSet.Transient.of();
    private MultiSet.Transient<IndexEntry> removedEntries = MultiSet.Transient.of();
    private MultiSet.Transient<IndexEntry> oldEntries = MultiSet.Transient.of();

    private IStrategoTerm sourceInCollection = null;

    public IndexCollector(ITermFactory termFactory, IndexEntryFactory entryFactory) {
        this.termFactory = termFactory;
        this.entryFactory = entryFactory;
    }

    public void start(IStrategoTerm source, Iterable<IndexEntry> currentEntries) {
        reset();
        currentEntries.forEach(removedEntries::add);
        currentEntries.forEach(oldEntries::add);

        sourceInCollection = source;
    }

    public IStrategoTuple stop() {
        sourceInCollection = null;

        addedEntries.removeAll(oldEntries);

        // TODO: Use an IStrategoList implementation that iterates over the collections instead of constructing it.
        return termFactory.makeTuple(entryFactory.toKeyTerms(removedEntries), entryFactory.toKeyTerms(addedEntries));
    }

    public IndexEntry add(IStrategoTerm key, IStrategoTerm value) {
        return add(entryFactory.create(key, value, sourceInCollection));
    }

    public IndexEntry add(IStrategoTerm key) {
        return add(entryFactory.create(key, sourceInCollection));
    }

    public IndexEntry add(IndexEntry entry) {
        assert entry.source.match(sourceInCollection);

        addedEntries.add(entry);
        removedEntries.remove(entry);
        return entry;
    }

    public Iterable<IndexEntry> getAddedEntries() {
        return addedEntries;
    }

    public boolean inCollection() {
        return sourceInCollection != null;
    }

    public void recover() {
        reset();
    }

    public void reset() {
        addedEntries = MultiSet.Transient.of();
        removedEntries = MultiSet.Transient.of();
        oldEntries = MultiSet.Transient.of();

        sourceInCollection = null;
    }
}
