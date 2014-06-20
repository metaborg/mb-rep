package org.spoofax.interpreter.library.index;

import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class HierarchicalIndex implements IHierarchicalIndex {
	private final IIndex current;
	private final IIndex parent;
	private final Set<IStrategoTerm> cleared = Sets.newHashSet();
	private final Predicate<IndexEntry> visible;

	private final IndexCollector collector;

	public HierarchicalIndex(IIndex current, IIndex parent, ITermFactory termFactory) {
		this.current = current;
		this.parent = parent;
		this.collector = new IndexCollector(termFactory, current.entryFactory());

		this.visible = new Predicate<IndexEntry>() {
			@Override
			public boolean apply(IndexEntry entry) {
				return parentEntryVisible(entry);
			}
		};
	}

	@Override
	public IndexEntryFactory entryFactory() {
		return current.entryFactory();
	}

	@Override
	public void startCollection(IStrategoTerm source) {
		collector.start(source, getInSource(source));
		// TODO: clear not required, can replace with new entries instead after collection.
		clearSource(source);
	}

	@Override
	public IndexEntry collect(IStrategoTerm key, IStrategoTerm value) {
		return collector.add(key, value);
	}

	@Override
	public IndexEntry collect(IStrategoTerm key) {
		return collector.add(key);
	}

	@Override
	public IStrategoTuple stopCollection(IStrategoTerm source) {
		addAll(source, collector.getAddedEntries());
		return collector.stop();
	}

	@Override
	public void add(IndexEntry entry) {
		current.add(entry);
	}

	@Override
	public void addAll(IStrategoTerm source, Iterable<IndexEntry> entry) {
		current.addAll(source, entry);
	}

	@Override
	public Iterable<IndexEntry> get(IStrategoTerm key) {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.get(key));
		final Iterable<IndexEntry> currentEntries = current.get(key);
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Iterable<IndexEntry> getAll() {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.getAll());
		final Iterable<IndexEntry> currentEntries = current.getAll();
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Iterable<IndexEntry> getChilds(IStrategoTerm key) {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.getChilds(key));
		final Iterable<IndexEntry> currentEntries = current.getChilds(key);
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Iterable<IndexEntry> getInSource(IStrategoTerm source) {
		final Iterable<IndexEntry> parentEntries = parentInvisibleFilter(parent.getInSource(source));
		final Iterable<IndexEntry> currentEntries = current.getInSource(source);
		return Iterables.concat(parentEntries, currentEntries);
	}

	@Override
	public Set<IStrategoTerm> getSourcesOf(IStrategoTerm key) {
		final Set<IStrategoTerm> parentSources = parent.getSourcesOf(key);
		final Set<IStrategoTerm> currentSources = current.getSourcesOf(key);
		// Use current set as first set because it is usually smaller, which results in improved performance.
		return Sets.union(currentSources, parentSources);
	}

	@Override
	public Iterable<IStrategoTerm> getAllSources() {
		final Iterable<IStrategoTerm> parentSources = parent.getAllSources();
		final Iterable<IStrategoTerm> currentSources = current.getAllSources();
		return Iterables.concat(parentSources, currentSources); // TODO: should this be a set?
	}

	@Override
	public void clearSource(IStrategoTerm source) {
		current.clearSource(source);
		cleared.add(source);
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
		collector.reset();
		current.reset();
		parent.reset();
	}

	private boolean parentEntryVisible(IndexEntry entry) {
		return !cleared.contains(entry.source);
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
	public Iterable<IStrategoTerm> getClearedSources() {
		return cleared;
	}
}
