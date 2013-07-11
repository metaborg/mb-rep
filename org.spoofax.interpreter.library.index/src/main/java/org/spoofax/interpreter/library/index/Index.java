package org.spoofax.interpreter.library.index;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class Index implements IIndex {
	public static final boolean DEBUG_ENABLED = Index.class.desiredAssertionStatus();

	private static final int EXPECTED_DISTINCT_PARTITIONS = 100;
	private static final int EXPECTED_VALUES_PER_PARTITION = 1000;

	private final ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>> entries =
		new ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>>();
	private final ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>> childs =
		new ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>>();
	private final Multimap<IndexPartitionDescriptor, IndexEntry> entriesPerPartitionDescriptor = ArrayListMultimap
		.create();
	private final Map<IndexPartitionDescriptor, IndexPartition> partitions =
		new HashMap<IndexPartitionDescriptor, IndexPartition>();

	private final Multiset<IndexEntry> addedEntries = HashMultiset.create();
	private final Multiset<IndexEntry> removedEntries = HashMultiset.create();
	private final Multiset<IndexEntry> oldEntries = HashMultiset.create();
	private boolean inCollection = false;

	private IOAgent agent;
	private ITermFactory termFactory;
	private IndexEntryFactory factory;

	public void initialize(ITermFactory factory, IOAgent agent) {
		this.agent = agent;
		this.factory = new IndexEntryFactory(factory);
		this.termFactory = factory;
	}

	private void ensureInitialized() {
		if(factory == null)
			throw new IllegalStateException("Index not initialized");
	}

	public IndexEntryFactory getFactory() {
		return factory;
	}

	private Multimap<IndexPartitionDescriptor, IndexEntry> innerEntries(IndexURI uri) {
		Multimap<IndexPartitionDescriptor, IndexEntry> innerMap =
			ArrayListMultimap.create(EXPECTED_DISTINCT_PARTITIONS, EXPECTED_VALUES_PER_PARTITION);

		Multimap<IndexPartitionDescriptor, IndexEntry> ret = entries.putIfAbsent(uri, innerMap);
		if(ret == null)
			ret = innerMap;
		return ret;
	}

	private Multimap<IndexPartitionDescriptor, IndexEntry> innerChildEntries(IndexURI uri) {
		Multimap<IndexPartitionDescriptor, IndexEntry> innerMap =
			ArrayListMultimap.create(EXPECTED_DISTINCT_PARTITIONS, EXPECTED_VALUES_PER_PARTITION);

		Multimap<IndexPartitionDescriptor, IndexEntry> ret = childs.putIfAbsent(uri, innerMap);
		if(ret == null)
			ret = innerMap;
		return ret;
	}

	private IndexEntry createEntry(IStrategoAppl entry, IndexPartitionDescriptor partitionDescriptor) {
		ensureInitialized();

		final IStrategoConstructor constructor = entry.getConstructor();
		final IStrategoTerm type = factory.getEntryType(entry);
		final IStrategoTerm identifier = factory.getEntryIdentifier(entry);
		final IStrategoTerm value = factory.getEntryValue(entry);

		return factory.createEntry(constructor, identifier, type, value, partitionDescriptor);
	}

	public void startCollection(IndexPartitionDescriptor partitionDescriptor) {
		addedEntries.clear();
		removedEntries.clear();
		oldEntries.clear();
		removedEntries.addAll(entriesPerPartitionDescriptor.get(partitionDescriptor));
		oldEntries.addAll(entriesPerPartitionDescriptor.get(partitionDescriptor));
		clearPartition(partitionDescriptor);
		inCollection = true;
	}

	public IStrategoTuple stopCollection() {
		Multisets.removeOccurrences(addedEntries, oldEntries);
		inCollection = false;

		// TODO: Use an IStrategoList implementation that iterates over the collections instead of constructing it.
		return termFactory.makeTuple(IndexEntry.toTerms(termFactory, removedEntries),
			IndexEntry.toTerms(termFactory, addedEntries));
	}

	public void add(IStrategoAppl entry, IndexPartitionDescriptor partitionDescriptor) {
		add(createEntry(entry, partitionDescriptor));
	}

	public void add(IndexEntry entry) {
		final IndexPartitionDescriptor partition = entry.getPartition();
		final IndexURI uri = entry.getKey();

		addOrGetPartition(partition);

		innerEntries(uri).put(partition, entry);
		if(inCollection) {
			addedEntries.add(entry);
			removedEntries.remove(entry);
		}

		// Add entry to children.
		IndexURI parent = uri.getParent(termFactory);
		if(parent != null)
			innerChildEntries(parent).put(partition, entry);

		// Add entry to partitions.
		entriesPerPartitionDescriptor.put(partition, entry);
	}

	public void addAll(IStrategoList entries, IndexPartitionDescriptor partitionDescriptor) {
		for(IStrategoTerm entry : entries) {
			add((IStrategoAppl) entry, partitionDescriptor);
		}
	}

	public Iterable<IndexEntry> get(IStrategoAppl template) {
		IndexURI uri = factory.createURIFromTemplate(template);
		return innerEntries(uri).values();
	}

	public Iterable<IndexEntry> getChildren(IStrategoAppl template) {
		IndexURI uri = factory.createURIFromTemplate(template);
		return innerChildEntries(uri).values();
	}

	public Iterable<IndexEntry> getInPartition(IndexPartitionDescriptor partitionDescriptor) {
		return entriesPerPartitionDescriptor.get(partitionDescriptor);
	}

	public Iterable<IndexPartitionDescriptor> getPartitionsOf(IStrategoAppl template) {
		IndexURI uri = factory.createURIFromTemplate(template);
		return innerEntries(uri).keySet();
	}

	public Iterable<IndexEntry> getAll() {
		List<IndexEntry> allEntries = new LinkedList<IndexEntry>();
		Collection<Multimap<IndexPartitionDescriptor, IndexEntry>> values = entries.values();
		for(Multimap<IndexPartitionDescriptor, IndexEntry> map : values)
			allEntries.addAll(map.values());

		return allEntries;
	}

	public IndexPartition getPartition(IndexPartitionDescriptor partitionDescriptor) {
		return addOrGetPartition(partitionDescriptor);
	}

	private IndexPartition addOrGetPartition(IndexPartitionDescriptor partitionDescriptor) {
		IndexPartition partition = partitions.get(partitionDescriptor);
		if(partition == null) {
			partition = new IndexPartition(partitionDescriptor, null);
			partitions.put(partitionDescriptor, partition);
		}
		return partition;
	}

	public IndexPartitionDescriptor getPartitionDescriptor(IStrategoTerm partitionTerm) {
		return IndexPartitionDescriptor.fromTerm(agent, partitionTerm);
	}

	public void clearPartition(IStrategoTerm partitionTerm) {
		clearPartitionInternal(getPartitionDescriptor(partitionTerm));
	}

	public void clearPartition(IndexPartitionDescriptor partitionDescriptor) {
		clearPartitionInternal(partitionDescriptor);
	}

	private void clearPartitionInternal(IndexPartitionDescriptor partitionDescriptor) {
		assert partitionDescriptor.getPartition() != null || partitionDescriptor.getURI() != null;

		Collection<Multimap<IndexPartitionDescriptor, IndexEntry>> entryValues = entries.values();
		for(Multimap<IndexPartitionDescriptor, IndexEntry> map : entryValues)
			map.removeAll(partitionDescriptor);

		Collection<Multimap<IndexPartitionDescriptor, IndexEntry>> childValues = childs.values();
		for(Multimap<IndexPartitionDescriptor, IndexEntry> map : childValues)
			map.removeAll(partitionDescriptor);

		entriesPerPartitionDescriptor.removeAll(partitionDescriptor);

		assert !getInPartition(partitionDescriptor).iterator().hasNext();
	}

	public Iterable<IndexPartition> getAllPartitions() {
		return partitions.values();
	}

	public Iterable<IndexPartitionDescriptor> getAllPartitionDescriptors() {
		return partitions.keySet();
	}

	public void clearAll() {
		entries.clear();
		childs.clear();
		entriesPerPartitionDescriptor.clear();
		partitions.clear();
		inCollection = false;
	}
}
