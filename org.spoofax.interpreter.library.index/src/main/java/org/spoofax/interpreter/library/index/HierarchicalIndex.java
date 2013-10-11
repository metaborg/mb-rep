package org.spoofax.interpreter.library.index;

import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class HierarchicalIndex implements IHierarchicalIndex {
	private final IIndex current;
	private final IIndex parent;
	private final Set<IndexPartition> cleared = new HashSet<IndexPartition>();
	private final Predicate<IndexEntry> visible;

	private final IndexCollection collection = new IndexCollection();
	private final ITermFactory termFactory;

	public HierarchicalIndex(IIndex current, IIndex parent, ITermFactory termFactory) {
		this.current = current;
		this.parent = parent;
		this.termFactory = termFactory;

		this.visible = new Predicate<IndexEntry>() {
			@Override
			public boolean apply(IndexEntry entry) {
				return parentEntryVisible(entry);
			}
		};
	}

	@Override
	public IndexEntryFactory getFactory() {
		return current.getFactory();
	}

	@Override
	public void startCollection(IndexPartition partition) {
		collection.start(getInPartition(partition));
		clearPartition(partition);
	}

	@Override
	public IStrategoTuple stopCollection() {
		return collection.stop(termFactory);
	}

	@Override
	public void add(IndexEntry entry) {
		current.add(entry);
	}

	@Override
	public Iterable<IndexEntry> get(IStrategoAppl template) {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.get(template));
		final Iterable<IndexEntry> currentEntries = current.get(template);
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Iterable<IndexEntry> getAll() {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.getAll());
		final Iterable<IndexEntry> currentEntries = current.getAll();
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Iterable<IndexEntry> getChildren(IStrategoAppl template) {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.getChildren(template));
		final Iterable<IndexEntry> currentEntries = current.getChildren(template);
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Iterable<IndexEntry> getInPartition(IndexPartition partition) {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.getInPartition(partition));
		final Iterable<IndexEntry> currentEntries = current.getInPartition(partition);
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Set<IndexPartition> getPartitionsOf(IStrategoAppl template) {
		final Set<IndexPartition> parentPartitions = parent.getPartitionsOf(template);
		final Set<IndexPartition> currentPartitions = current.getPartitionsOf(template);
		// Use current set as first set because it is usually smaller, which results in improved performance.
		return Sets.union(currentPartitions, parentPartitions);
	}

	@Override
	public Iterable<IndexPartition> getAllPartitions() {
		final Iterable<IndexPartition> parentPartitions = parent.getAllPartitions();
		final Iterable<IndexPartition> currentPartitions = current.getAllPartitions();
		return Iterables.concat(parentPartitions, currentPartitions); // TODO: should this be a set?
	}

	@Override
	public void clearPartition(IndexPartition partition) {
		current.clearPartition(partition);
		cleared.add(partition);
	}

	@Override
	public Iterable<String> getAllLanguages() {
		final Iterable<String> parentLanguages = parent.getAllLanguages();
		final Iterable<String> currentLanguages = current.getAllLanguages();
		return Iterables.concat(parentLanguages, currentLanguages); // TODO: should this be a set?
	}

	@Override
	public boolean hasLanguage(String language) {
		return parent.hasLanguage(language) || current.hasLanguage(language);
	}

	@Override
	public boolean addLanguage(String language) {
		return current.addLanguage(language) && parent.hasLanguage(language);
	}

	@Override
	public void reset() {
		cleared.clear();
		current.reset();
		parent.reset();
	}

	private boolean parentEntryVisible(IndexEntry entry) {
		return !cleared.contains(entry.getPartition());
	}

	private Iterable<IndexEntry> parentInvisibleFilter(Iterable<IndexEntry> entries) {
		return Iterables.filter(entries, visible);
	}

	@Override
	public IIndex getParent() {
		return parent;
	}

	@Override
	public Iterable<IndexEntry> getAllCurrent() {
		return current.getAll();
	}

	@Override
	public Iterable<IndexPartition> getClearedPartitions() {
		return cleared;
	}
}
