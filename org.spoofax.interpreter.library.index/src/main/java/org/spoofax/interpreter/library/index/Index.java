package org.spoofax.interpreter.library.index;

import java.util.Collection;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class Index implements IIndex {
    private final Multimap<IStrategoTerm, IndexEntry> entries = HashMultimap.create();
    private final Multimap<IStrategoTerm, IndexEntry> childs = HashMultimap.create();
    private final Multimap<IStrategoTerm, IndexEntry> entriesPerSource = HashMultimap.create();

    private final IndexEntryFactory entryFactory;
    private final IndexParentKeyFactory parentKeyFactory;
    private final IndexCollector collector;


    public Index(ITermFactory termFactory) {
        this.entryFactory = new IndexEntryFactory(termFactory);
        this.parentKeyFactory = new IndexParentKeyFactory(termFactory);
        this.collector = new IndexCollector(termFactory, entryFactory);
    }


    @Override public IndexEntryFactory entryFactory() {
        return entryFactory;
    }

    @Override public void startCollection(IStrategoTerm source) {
        collector.start(source, getInSource(source));
        clearSource(source);
    }

    @Override public IndexEntry collect(IStrategoTerm key, IStrategoTerm value) {
        return collector.add(key, value);
    }

    @Override public IndexEntry collect(IStrategoTerm key) {
        return collector.add(key);
    }

    @Override public IStrategoTuple stopCollection(IStrategoTerm source) {
        addAll(source, collector.getAddedEntries());
        return collector.stop();
    }

    @Override public void add(IndexEntry entry) {
        entries.put(entry.key, entry);

        final IStrategoTerm parentKey = parentKeyFactory.getParentKey(entry.key);
        if(parentKey != null) {
            childs.put(parentKey, entry);
        }

        entriesPerSource.put(entry.source, entry);
    }

    @Override public void addAll(IStrategoTerm source, Iterable<IndexEntry> entriesToAdd) {
        final Collection<IndexEntry> entriesInSource = entriesPerSource.get(source);
        for(final IndexEntry entry : entriesToAdd) {
            entries.put(entry.key, entry);

            final IStrategoTerm parentKey = parentKeyFactory.getParentKey(entry.key);
            if(parentKey != null) {
                childs.put(parentKey, entry);
            }

            entriesInSource.add(entry);
        }
    }

    @Override public Iterable<IndexEntry> get(IStrategoTerm key) {
        return entries.get(key);
    }

    @Override public Iterable<IndexEntry> getChilds(IStrategoTerm key) {
        return childs.get(key);
    }

    @Override public Iterable<IndexEntry> getInSource(IStrategoTerm source) {
        return entriesPerSource.get(source);
    }

    @Override public Set<IStrategoTerm> getSourcesOf(IStrategoTerm key) {
        final Set<IStrategoTerm> sources = Sets.newHashSet();
        for(final IndexEntry entry : get(key)) {
            sources.add(entry.source);
        }
        return sources;
    }

    @Override public Iterable<IndexEntry> getAll() {
        return entries.values();
    }

    @Override public void clearSource(IStrategoTerm source) {
        for(final IndexEntry entry : getInSource(source)) {
            entries.remove(entry.key, entry);
            final IStrategoTerm parentKey = parentKeyFactory.getParentKey(entry.key);
            if(parentKey != null) {
                childs.remove(parentKey, entry);
            }
        }
        entriesPerSource.removeAll(source);
    }

    @Override public Iterable<IStrategoTerm> getAllSources() {
        return entriesPerSource.keySet();
    }

    @Override public void recover() {
        collector.recover();
    }

    @Override public void reset() {
        entries.clear();
        childs.clear();
        entriesPerSource.clear();
        collector.reset();
    }
}
