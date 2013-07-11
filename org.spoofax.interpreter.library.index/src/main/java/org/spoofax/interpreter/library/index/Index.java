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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class Index implements IIndex {
	public static final boolean DEBUG_ENABLED = Index.class.desiredAssertionStatus();

	private static final int EXPECTED_DISTINCT_PARTITIONS = 100;
	private static final int EXPECTED_VALUES_PER_PARTITION = 1000;

	private final ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>> entries =
		new ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>>();
	private final ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>> childs =
		new ConcurrentHashMap<IndexURI, Multimap<IndexPartition, IndexEntry>>();
	private final Multimap<IndexPartition, IndexEntry> entriesPerpartition = ArrayListMultimap.create();
	private final Set<IndexPartition> partitions = new HashSet<IndexPartition>();

	private final IndexCollection collection = new IndexCollection();

	private final ITermFactory termFactory;
	private final IndexEntryFactory factory;

	public Index(ITermFactory factory) {
		this.termFactory = factory;
		this.factory = new IndexEntryFactory(factory);
	}

	public IndexEntryFactory getFactory() {
		return factory;
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
		IndexURI uri = factory.createURIFromTemplate(template);
		return innerEntries(uri).values();
	}

	public Iterable<IndexEntry> getChildren(IStrategoAppl template) {
		IndexURI uri = factory.createURIFromTemplate(template);
		return innerChildEntries(uri).values();
	}

	public Iterable<IndexEntry> getInPartition(IndexPartition partition) {
		return entriesPerpartition.get(partition);
	}

	public Iterable<IndexPartition> getPartitionsOf(IStrategoAppl template) {
		IndexURI uri = factory.createURIFromTemplate(template);
		return innerEntries(uri).keySet();
	}

	public Iterable<IndexEntry> getAll() {
		List<IndexEntry> allEntries = new LinkedList<IndexEntry>();
		Collection<Multimap<IndexPartition, IndexEntry>> values = entries.values();
		for(Multimap<IndexPartition, IndexEntry> map : values)
			allEntries.addAll(map.values());

		return allEntries;
	}

	public void clearPartition(IndexPartition partition) {
		assert partition.getPartition() != null || partition.getURI() != null;

		Collection<Multimap<IndexPartition, IndexEntry>> entryValues = entries.values();
		for(Multimap<IndexPartition, IndexEntry> map : entryValues)
			map.removeAll(partition);

		Collection<Multimap<IndexPartition, IndexEntry>> childValues = childs.values();
		for(Multimap<IndexPartition, IndexEntry> map : childValues)
			map.removeAll(partition);

		entriesPerpartition.removeAll(partition);
		partitions.remove(partition);

		assert !getInPartition(partition).iterator().hasNext();
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
	}
}
