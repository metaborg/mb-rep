package org.spoofax.interpreter.library.index;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class Index implements IIndex {
	private static final int EXPECTED_DISTINCT_PARTITIONS = 100;
	private static final int EXPECTED_VALUES_PER_PARTITION = 1000;

	private final ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>> entries =
		new ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>>();
	private final ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>> childs =
		new ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>>();
	private final Multimap<IndexPartition, IndexEntry> entriesPerpartition = ArrayListMultimap.create();
	private final Set<IndexPartition> partitions = new HashSet<IndexPartition>();

	private final Set<IndexPartition> cleared = new HashSet<IndexPartition>();
	private final Predicate<IndexEntry> visible;

	private final IIndex parent;
	private final IndexCollection collection = new IndexCollection();
	private final ITermFactory termFactory;
	private final IndexEntryFactory factory;

	public Index(IIndex parent, ITermFactory factory) {
		this.parent = parent;
		this.termFactory = factory;
		this.factory = new IndexEntryFactory(factory);

		this.visible = new Predicate<IndexEntry>() {
			public boolean apply(IndexEntry entry) {
				return parentEntryVisible(entry);
			}
		};
	}

	public IndexEntryFactory getFactory() {
		return factory;
	}

	public IIndex getParent() {
		return parent;
	}

	private Multimap<IndexPartition, IndexEntry> innerEntries(IndexURI uri) {
		Multimap<IndexPartition, IndexEntry> innerMap =
			ArrayListMultimap.create(EXPECTED_DISTINCT_PARTITIONS, EXPECTED_VALUES_PER_PARTITION);

		Multimap<IndexPartition, IndexEntry> ret = entries.putIfAbsent(uri, innerMap);
		if(ret == null)
			ret = innerMap;
		return ret;
	}

	private Multimap<IndexPartition, IndexEntry> innerChildEntries(IndexURI uri) {
		Multimap<IndexPartition, IndexEntry> innerMap =
			ArrayListMultimap.create(EXPECTED_DISTINCT_PARTITIONS, EXPECTED_VALUES_PER_PARTITION);

		Multimap<IndexPartition, IndexEntry> ret = childs.putIfAbsent(uri, innerMap);
		if(ret == null)
			ret = innerMap;
		return ret;
	}

	private boolean parentEntryVisible(IndexEntry entry) {
		return !cleared.contains(entry.getPartition());
	}

	public void startCollection(IndexPartition partition) {
		collection.start(getInPartition(partition));
		clearPartition(partition);
	}

	public IStrategoTuple stopCollection() {
		return collection.stop(termFactory);
	}

	public void add(IndexEntry entry) {
		final IndexPartition partition = entry.getPartition();
		final IndexURI uri = entry.getKey();

		partitions.add(partition);

		innerEntries(uri).put(partition, entry);
		if(collection.inCollection()) {
			collection.add(entry);
		}

		// Add entry to children.
		IndexURI parent = uri.getParent(termFactory);
		if(parent != null)
			innerChildEntries(parent).put(partition, entry);

		// Add entry to partitions.
		entriesPerpartition.put(partition, entry);
	}

	public Iterable<IndexEntry> get(IStrategoAppl template) {
		final IndexURI uri = factory.createURIFromTemplate(template);
		final Iterable<IndexEntry> parentEntries = Iterables.filter(parent.get(template), visible);
		final Iterable<IndexEntry> ownEntries = innerEntries(uri).values();
		return Iterables.concat(parentEntries, ownEntries);
	}

	public Iterable<IndexEntry> getChildren(IStrategoAppl template) {
		final IndexURI uri = factory.createURIFromTemplate(template);
		final Iterable<IndexEntry> parentChildren = Iterables.filter(parent.getChildren(template), visible);
		final Iterable<IndexEntry> ownChildren = innerChildEntries(uri).values();
		return Iterables.concat(parentChildren, ownChildren);
	}

	public Iterable<IndexEntry> getInPartition(IndexPartition partition) {
		final Iterable<IndexEntry> parentEntries = Iterables.filter(parent.getInPartition(partition), visible);
		final Iterable<IndexEntry> ownEntries = entriesPerpartition.get(partition);
		return Iterables.concat(parentEntries, ownEntries);
	}

	public Set<IndexPartition> getPartitionsOf(IStrategoAppl template) {
		final IndexURI uri = factory.createURIFromTemplate(template);
		final Set<IndexPartition> parentPartitions = parent.getPartitionsOf(template);
		final Set<IndexPartition> ownPartitions = innerEntries(uri).keySet();
		// Use own set as first set because it is usually faster, which results in improved performance.
		return Sets.union(ownPartitions, parentPartitions);
	}

	public Iterable<IndexEntry> getAll() {
		final List<IndexEntry> allEntries = new LinkedList<IndexEntry>();

		final Iterable<IndexEntry> parentEntries = Iterables.filter(parent.getAll(), visible);
		Iterables.addAll(allEntries, parentEntries);

		final Collection<Multimap<IndexPartition, IndexEntry>> ownValues = entries.values();
		for(Multimap<IndexPartition, IndexEntry> map : ownValues)
			allEntries.addAll(map.values());

		return allEntries;
	}

	public Iterable<IndexEntry> getAllCurrent() {
		final List<IndexEntry> allEntries = new LinkedList<IndexEntry>();
		final Collection<Multimap<IndexPartition, IndexEntry>> ownValues = entries.values();
		for(Multimap<IndexPartition, IndexEntry> map : ownValues)
			allEntries.addAll(map.values());
		return allEntries;
	}

	public void clearPartition(IndexPartition partition) {
		final Collection<Multimap<IndexPartition, IndexEntry>> entryValues = entries.values();
		for(Multimap<IndexPartition, IndexEntry> map : entryValues)
			map.removeAll(partition);

		final Collection<Multimap<IndexPartition, IndexEntry>> childValues = childs.values();
		for(Multimap<IndexPartition, IndexEntry> map : childValues)
			map.removeAll(partition);

		entriesPerpartition.removeAll(partition);
		partitions.remove(partition);
		cleared.add(partition);
	}

	public Iterable<IndexPartition> getAllPartitions() {
		return partitions;
	}

	public void clearAll() {
		entries.clear();
		childs.clear();
		entriesPerpartition.clear();
		partitions.clear();
		collection.clear();
		parent.clearAll();
	}
}
