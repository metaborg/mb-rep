package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class IndexCollection {
	private final Multiset<IndexEntry> addedEntries = HashMultiset.create();
	private final Multiset<IndexEntry> removedEntries = HashMultiset.create();
	private final Multiset<IndexEntry> oldEntries = HashMultiset.create();

	private boolean inCollection = false;

	public IndexCollection() {

	}

	public void start(Iterable<IndexEntry> currentEntries) {
		addedEntries.clear();
		removedEntries.clear();
		oldEntries.clear();
		Iterables.addAll(removedEntries, currentEntries);
		Iterables.addAll(oldEntries, currentEntries);

		inCollection = true;
	}

	public IStrategoTuple stop(ITermFactory factory) {
		inCollection = false;

		Multisets.removeOccurrences(addedEntries, oldEntries);

		// TODO: Use an IStrategoList implementation that iterates over the collections instead of constructing it.
		return factory
			.makeTuple(IndexEntry.toTerms(factory, removedEntries), IndexEntry.toTerms(factory, addedEntries));
	}

	public void add(IndexEntry entry) {
		addedEntries.add(entry);
		removedEntries.remove(entry);
	}

	public boolean inCollection() {
		return this.inCollection;
	}

	public void clear() {
		addedEntries.clear();
		removedEntries.clear();
		oldEntries.clear();

		inCollection = false;
	}
}
