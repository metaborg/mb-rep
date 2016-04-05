package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class IndexCollector {
	private final ITermFactory termFactory;
	private final IndexEntryFactory entryFactory;

	private final Multiset<IndexEntry> addedEntries = HashMultiset.create();
	private final Multiset<IndexEntry> removedEntries = HashMultiset.create();
	private final Multiset<IndexEntry> oldEntries = HashMultiset.create();

	private IStrategoTerm sourceInCollection = null;

	public IndexCollector(ITermFactory termFactory, IndexEntryFactory entryFactory) {
		this.termFactory = termFactory;
		this.entryFactory = entryFactory;
	}

	public void start(IStrategoTerm source, Iterable<IndexEntry> currentEntries) {
		addedEntries.clear();
		removedEntries.clear();
		oldEntries.clear();
		Iterables.addAll(removedEntries, currentEntries);
		Iterables.addAll(oldEntries, currentEntries);

		sourceInCollection = source;
	}

	public IStrategoTuple stop() {
		sourceInCollection = null;

		Multisets.removeOccurrences(addedEntries, oldEntries);

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
		addedEntries.clear();
		removedEntries.clear();
		oldEntries.clear();

		sourceInCollection = null;
	}
}
