package org.spoofax.interpreter.library.index;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
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
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Gabriël Konat
 */
public class Index implements IIndex {
    public static final boolean DEBUG_ENABLED = Index.class.desiredAssertionStatus();

    private static final int EXPECTED_DISTINCT_PARTITIONS = 100;
    private static final int EXPECTED_VALUES_PER_PARTITION = 1000;

    private final ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>> entries =
        new ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>>();
    private final ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>> childs =
        new ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>>();
    private final Multimap<IndexPartitionDescriptor, IndexEntry> entriesPerPartitionDescriptor = LinkedHashMultimap
        .create();
    private final Multimap<URI, IndexEntry> entriesPerFile = LinkedHashMultimap.create();
    private final Multimap<IStrategoList, IndexEntry> entriesPerPartition = LinkedHashMultimap.create();
    private final Map<IndexPartitionDescriptor, IndexPartition> partitions =
        new HashMap<IndexPartitionDescriptor, IndexPartition>();

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

    public void add(IStrategoAppl entry, IndexPartitionDescriptor partitionDescriptor) {
        ensureInitialized();

        IStrategoConstructor constructor = entry.getConstructor();
        IStrategoTerm type = factory.getEntryType(entry);
        IStrategoTerm identifier = factory.getEntryIdentifier(entry);
        IStrategoTerm value = factory.getEntryValue(entry);

        IndexEntry newEntry =
            factory.createEntry(constructor, identifier, type, value, partitionDescriptor);

        add(newEntry);
    }

    public void add(IndexEntry entry) {
        final IndexPartitionDescriptor partition = entry.getPartition();
        final IndexURI uri = entry.getKey();

        addOrGetPartition(partition);

        innerEntries(uri).put(partition, entry);

        // Add entry to children.
        IndexURI parent = uri.getParent(termFactory);
        if(parent != null)
            innerChildEntries(parent).put(partition, entry);

        // Add entry to partitions.
        entriesPerPartitionDescriptor.put(partition, entry);
        entriesPerFile.put(partition.getURI(), entry);
        entriesPerPartition.put(partition.getPartition(), entry);
    }

    public void addAll(IStrategoList entries, IndexPartitionDescriptor partitionDescriptor) {
        while(!entries.isEmpty()) {
            add((IStrategoAppl) entries.head(), partitionDescriptor);
            entries = entries.tail();
        }
    }

    public void remove(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor) {
        IndexURI uri = factory.createURIFromTemplate(template);
        IndexURI parentURI = uri.getParent(termFactory);
        Multimap<IndexPartitionDescriptor, IndexEntry> entryValues = entries.get(uri);
        Multimap<IndexPartitionDescriptor, IndexEntry> childValues = null;
        if(parentURI != null)
            childValues = childs.get(uri.getParent(termFactory));
        Collection<IndexEntry> removedEntries = entryValues.removeAll(partitionDescriptor);

        for(IndexEntry entry : removedEntries) {
            if(parentURI != null)
                childValues.remove(partitionDescriptor, entry);
            entriesPerPartitionDescriptor.remove(partitionDescriptor, entry);
            entriesPerFile.remove(partitionDescriptor.getURI(), entry);
            entriesPerPartition.remove(partitionDescriptor.getPartition(), entry);
        }
    }

    public IIndexEntryIterable get(IStrategoAppl template) {
        IndexURI uri = factory.createURIFromTemplate(template);
        return getEntryIterable(innerEntries(uri).values());
    }

    public IIndexEntryIterable getChildren(IStrategoAppl template) {
        IndexURI uri = factory.createURIFromTemplate(template);
        return getEntryIterable(innerChildEntries(uri).values());
    }

    public IIndexEntryIterable getInPartition(IndexPartitionDescriptor partitionDescriptor) {
        if(partitionDescriptor.getPartition() == null)
            return getEntryIterable(entriesPerFile.get(partitionDescriptor.getURI()));
        else if(partitionDescriptor.getURI() == null)
            return getEntryIterable(entriesPerPartition.get(partitionDescriptor.getPartition()));
        else
            return getEntryIterable(entriesPerPartitionDescriptor.get(partitionDescriptor));
    }

    public Collection<IndexPartitionDescriptor> getPartitionsOf(IStrategoAppl template) {
        IndexURI uri = factory.createURIFromTemplate(template);
        return getCollection(innerEntries(uri).keySet());
    }

    public IIndexEntryIterable getAll() {
        List<IndexEntry> allEntries = new LinkedList<IndexEntry>();
        Collection<Multimap<IndexPartitionDescriptor, IndexEntry>> values = entries.values();
        for(Multimap<IndexPartitionDescriptor, IndexEntry> map : values)
            allEntries.addAll(map.values());

        return getEntryIterable(allEntries);
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

        if(partitionDescriptor.getPartition() == null)
            entriesPerFile.removeAll(partitionDescriptor.getURI());
        else if(partitionDescriptor.getURI() == null)
            entriesPerPartition.removeAll(partitionDescriptor.getPartition());
        else {
            entriesPerPartitionDescriptor.removeAll(partitionDescriptor);
            clearPartition(new IndexPartitionDescriptor(partitionDescriptor.getURI(), null));
            clearPartition(new IndexPartitionDescriptor(null, partitionDescriptor.getPartition()));
        }

        assert !getInPartition(partitionDescriptor).iterator().hasNext();
    }

    public Collection<IndexPartition> getAllPartitions() {
        return getCollection(partitions.values());
    }

    public Collection<IndexPartitionDescriptor> getAllPartitionDescriptors() {
        return getCollection(partitions.keySet());
    }

    public void clearAll() {
        entries.clear();
        childs.clear();
        entriesPerPartitionDescriptor.clear();
        entriesPerFile.clear();
        entriesPerPartition.clear();
        partitions.clear();
    }

    private static final IIndexEntryIterable getEntryIterable(Collection<IndexEntry> collection) {
        return new IndexEntryIterable(collection, null);
    }
    
    /**
     * Returns an unmodifiable collection if in debug mode, or the collection if not.
     */
    private static final <T> Collection<T> getCollection(Collection<T> collection) {
        if(DEBUG_ENABLED) {
            return Collections.unmodifiableCollection(collection);
        } else {
            return collection;
        }
    }
}
