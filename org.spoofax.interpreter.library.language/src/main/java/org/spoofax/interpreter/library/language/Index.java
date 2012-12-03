package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermList;
import static org.spoofax.terms.Term.termAt;
import static org.spoofax.terms.Term.tryGetConstructor;

import java.io.IOException;
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
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

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
    private static final IStrategoConstructor FILE_ENTRIES_CON = new TermFactory().makeConstructor("PartitionEntries",
        2);

    private final ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>> entries =
        new ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>>();
    private final ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>> childs =
        new ConcurrentHashMap<IndexURI, Multimap<IndexPartitionDescriptor, IndexEntry>>();
    private final Multimap<IndexPartitionDescriptor, IndexEntry> entriesPerPartitionDescriptor = LinkedHashMultimap
        .create();
    private final Multimap<URI, IndexEntry> entriesPerURI = LinkedHashMultimap.create();
    private final Multimap<IStrategoList, IndexEntry> entriesPerSubpartition = LinkedHashMultimap.create();
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

    public void ensureInitialized() {
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
        IStrategoTerm contentsType = factory.getEntryContentsType(entry);
        IStrategoList id = factory.getEntryId(entry);
        IStrategoTerm namespace = factory.getEntryNamespace(entry);
        IStrategoTerm contents = factory.getEntryContents(entry);

        IndexEntry newEntry =
            factory.createEntry(constructor, namespace, id, contentsType, contents, partitionDescriptor);

        add(newEntry);
    }

    public void add(IndexEntry entry) {
        final IndexPartitionDescriptor partition = entry.getPartitionDescriptor();
        final IndexURI uri = entry.getURI();

        addOrGetPartition(partition);

        innerEntries(uri).put(partition, entry);

        // Add entry to children.
        IndexURI parent = uri.getParent();
        if(parent != null)
            innerChildEntries(parent).put(partition, entry);

        // Add entry to partitions.
        entriesPerPartitionDescriptor.put(partition, entry);
        entriesPerURI.put(partition.getURI(), entry);
        entriesPerSubpartition.put(partition.getPartition(), entry);
    }

    public void addAll(IStrategoList entries, IndexPartitionDescriptor partitionDescriptor) {
        while(!entries.isEmpty()) {
            add((IStrategoAppl) entries.head(), partitionDescriptor);
            entries = entries.tail();
        }
    }

    public void remove(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor) {
        IndexURI uri = factory.createURIFromTemplate(template);
        Multimap<IndexPartitionDescriptor, IndexEntry> entryValues = entries.get(uri);
        Multimap<IndexPartitionDescriptor, IndexEntry> childValues = childs.get(uri.getParent());
        Collection<IndexEntry> removedEntries = entryValues.removeAll(partitionDescriptor);

        for(IndexEntry entry : removedEntries) {
            childValues.remove(partitionDescriptor, entry);
            entriesPerPartitionDescriptor.remove(partitionDescriptor, entry);
            entriesPerURI.remove(partitionDescriptor.getURI(), entry);
            entriesPerSubpartition.remove(partitionDescriptor.getPartition(), entry);
        }
    }

    public Collection<IndexEntry> getEntries(IStrategoAppl template) {
        IndexURI uri = factory.createURIFromTemplate(template);
        return getCollection(innerEntries(uri).values());
    }

    public Collection<IndexEntry> getEntryChildTerms(IStrategoAppl template) {
        IndexURI uri = factory.createURIFromTemplate(template);
        return getCollection(innerChildEntries(uri).values());
    }

    public Collection<IndexEntry> getEntriesInPartition(IndexPartitionDescriptor partitionDescriptor) {
        if(partitionDescriptor.getPartition() == null)
            return getCollection(entriesPerURI.get(partitionDescriptor.getURI()));
        else if(partitionDescriptor.getURI() == null)
            return getCollection(entriesPerSubpartition.get(partitionDescriptor.getPartition()));
        else
            return getCollection(entriesPerPartitionDescriptor.get(partitionDescriptor));
    }

    public Collection<IndexPartitionDescriptor> getPartitionsOf(IStrategoAppl template) {
        IndexURI uri = factory.createURIFromTemplate(template);
        return getCollection(innerEntries(uri).keySet());
    }

    public Collection<IndexEntry> getAllEntries() {
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

    public void removePartition(IStrategoTerm partitionTerm) {
        removePartition(getPartitionDescriptor(partitionTerm));
    }

    public void removePartition(IndexPartitionDescriptor partitionDescriptor) {
        clearPartition(partitionDescriptor);
    }

    private void clearPartition(IndexPartitionDescriptor partitionDescriptor) {
        assert partitionDescriptor.getPartition() != null || partitionDescriptor.getURI() != null;

        Collection<Multimap<IndexPartitionDescriptor, IndexEntry>> entryValues = entries.values();
        for(Multimap<IndexPartitionDescriptor, IndexEntry> map : entryValues)
            map.removeAll(partitionDescriptor);

        Collection<Multimap<IndexPartitionDescriptor, IndexEntry>> childValues = childs.values();
        for(Multimap<IndexPartitionDescriptor, IndexEntry> map : childValues)
            map.removeAll(partitionDescriptor);

        if(partitionDescriptor.getPartition() == null)
            entriesPerURI.removeAll(partitionDescriptor.getURI());
        else if(partitionDescriptor.getURI() == null)
            entriesPerSubpartition.removeAll(partitionDescriptor.getPartition());
        else {
            entriesPerPartitionDescriptor.removeAll(partitionDescriptor);
            clearPartition(new IndexPartitionDescriptor(partitionDescriptor.getURI(), null));
            clearPartition(new IndexPartitionDescriptor(null, partitionDescriptor.getPartition()));
        }

        assert getEntriesInPartition(partitionDescriptor).isEmpty();
    }

    public Collection<IndexPartition> getAllPartitions() {
        return getCollection(partitions.values());
    }

    public Collection<IndexPartitionDescriptor> getAllPartitionDescriptors() {
        return getCollection(partitions.keySet());
    }

    public void clear() {
        entries.clear();
        childs.clear();
        entriesPerPartitionDescriptor.clear();
        entriesPerURI.clear();
        entriesPerSubpartition.clear();
        partitions.clear();
    }

    public IStrategoTerm toTerm(boolean includePositions) {
        IStrategoList results = termFactory.makeList();
        for(IndexPartitionDescriptor partitionDescriptor : partitions.keySet()) {
            IStrategoList partitionResults =
                IndexEntry.toTerms(termFactory, entriesPerPartitionDescriptor.get(partitionDescriptor));
            // TODO: include time stamp for partition
            IStrategoTerm result =
                termFactory.makeAppl(FILE_ENTRIES_CON, partitionDescriptor.toTerm(termFactory), partitionResults);
            results = termFactory.makeListCons(result, results);
        }

        if(includePositions) {
            // TODO: optimize -- store more compact attachments for positions
            TermFactory simpleFactory = new TermFactory();
            TermAttachmentSerializer serializer = new TermAttachmentSerializer(simpleFactory);
            results = (IStrategoList) serializer.toAnnotations(results);
        }

        return results;
    }

    public static Index fromTerm(IStrategoTerm term, ITermFactory factory, IOAgent agent, boolean extractPositions)
        throws IOException {
        if(extractPositions) {
            TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
            term = (IStrategoList) serializer.fromAnnotations(term, false);
        }

        if(isTermList(term)) {
            Index result = new Index();
            result.initialize(factory, agent);
            for(IStrategoList list = (IStrategoList) term; !list.isEmpty(); list = list.tail()) {
                result.loadPartitionEntriesTerm(list.head());
            }
            return result;
        } else {
            throw new IOException("Expected list of " + FILE_ENTRIES_CON.getName());
        }
    }

    private void loadPartitionEntriesTerm(IStrategoTerm partitionEntries) throws IOException {
        if(tryGetConstructor(partitionEntries) == FILE_ENTRIES_CON) {
            try {
                addAll((IStrategoList) termAt(partitionEntries, 1), getPartitionDescriptor(termAt(partitionEntries, 0)));
            } catch(IllegalStateException e) {
                throw new IllegalStateException(e);
            } catch(RuntimeException e) { // HACK: catch all runtime exceptions
                throw new IOException("Unexpected exception reading index: " + e);
            }
        } else {
            throw new IOException("Illegal index entry: " + partitionEntries);
        }
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
