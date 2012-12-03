package org.spoofax.interpreter.library.language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class TransactionIndex implements IIndex {
    private IIndex index;
    private IIndex transactionIndex;
    private IndexPartitionDescriptor currentPartition;
    private boolean clearedCurrentPartition = false;
    private List<TemplateWithPartitionDescriptor> removedEntries = new ArrayList<TemplateWithPartitionDescriptor>();

    public TransactionIndex(IIndex index, IIndex transactionIndex, IndexPartitionDescriptor currentPartition) {
        this.index = index;
        this.transactionIndex = transactionIndex;
        this.currentPartition = currentPartition;
    }

    public IIndex getIndex() {
        return index;
    }

    public IIndex getTransactionIndex() {
        return transactionIndex;
    }

    public boolean hasClearedCurrentPartition() {
        return clearedCurrentPartition;
    }

    public IndexPartitionDescriptor getCurrentPartition() {
        return currentPartition;
    }

    public Collection<TemplateWithPartitionDescriptor> getRemovedEntries() {
        return removedEntries;
    }

    public void initialize(ITermFactory factory, IOAgent agent) {
        // Should not be called, both the index and transaction index should already be initialized.
        assert false;
    }

    public IndexEntryFactory getFactory() {
        return index.getFactory();
    }

    public void add(IStrategoAppl entry, IndexPartitionDescriptor partitionDescriptor) {
        transactionIndex.add(entry, partitionDescriptor);
    }

    public void add(IndexEntry entry) {
        transactionIndex.add(entry);
    }

    public void addAll(IStrategoList entries, IndexPartitionDescriptor partitionDescriptor) {
        transactionIndex.addAll(entries, partitionDescriptor);
    }

    public void remove(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor) {
        transactionIndex.remove(template, partitionDescriptor);
        removedEntries.add(new TemplateWithPartitionDescriptor(template, partitionDescriptor));
    }

    public Collection<IndexEntry> getEntries(IStrategoAppl template) {
        Collection<IndexEntry> entries1 = transactionIndex.getEntries(template);
        getReadLock().lock();
        try {
            Collection<IndexEntry> entries2 = filterInvisibleEntries(index.getEntries(template));
            return concat(entries1, entries2);
        } finally {
            getReadLock().unlock();
        }
    }

    public Collection<IndexEntry> getAllEntries() {
        Collection<IndexEntry> entries1 = transactionIndex.getAllEntries();
        getReadLock().lock();
        try {
            Collection<IndexEntry> entries2 = filterInvisibleEntries(index.getAllEntries());
            return concat(entries1, entries2);
        } finally {
            getReadLock().unlock();
        }
    }

    public Collection<IndexEntry> getEntryChildTerms(IStrategoAppl template) {
        Collection<IndexEntry> entries1 = transactionIndex.getEntryChildTerms(template);
        getReadLock().lock();
        try {
            Collection<IndexEntry> entries2 = filterInvisibleEntries(index.getEntryChildTerms(template));
            return concat(entries1, entries2);
        } finally {
            getReadLock().unlock();
        }
    }

    public Collection<IndexEntry> getEntriesInPartition(IndexPartitionDescriptor partitionDescriptor) {
        Collection<IndexEntry> entries1 = transactionIndex.getEntriesInPartition(partitionDescriptor);

        if(isCurrentPartition(partitionDescriptor) && clearedCurrentPartition)
            return entries1; // Current partition has been cleared, entries from index should not be visible.

        getReadLock().lock();
        try {
            Collection<IndexEntry> entries2 = index.getEntriesInPartition(partitionDescriptor);
            return concat(entries1, entries2);
        } finally {
            getReadLock().unlock();
        }
    }

    public Collection<IndexPartitionDescriptor> getPartitionsOf(IStrategoAppl template) {
        Collection<IndexPartitionDescriptor> entries1 = transactionIndex.getPartitionsOf(template);
        getReadLock().lock();
        try {
            // TODO: Need to filter hidden entries here?
            Collection<IndexPartitionDescriptor> entries2 = index.getPartitionsOf(template);
            return concat(entries1, entries2);
        } finally {
            getReadLock().unlock();
        }
    }

    public IndexPartition getPartition(IndexPartitionDescriptor partitionDescriptor) {
        // Need a write lock here because getPartition can add a new partition.
        getWriteLock().lock();
        try {
            return index.getPartition(partitionDescriptor);
        } finally {
            getWriteLock().unlock();
        }
    }

    public IndexPartitionDescriptor getPartitionDescriptor(IStrategoTerm partitionTerm) {
        return index.getPartitionDescriptor(partitionTerm);
    }

    public void removePartition(IStrategoTerm partitionTerm) {
        removePartition(transactionIndex.getPartitionDescriptor(partitionTerm));
    }

    public void removePartition(IndexPartitionDescriptor partitionDescriptor) {
        assert isCurrentPartition(partitionDescriptor); // May only clear current partition.

        clearedCurrentPartition = true;
        transactionIndex.removePartition(partitionDescriptor);
    }

    public Collection<IndexPartition> getAllPartitions() {
        // TODO: No duplicates?
        Collection<IndexPartition> partitions1 = transactionIndex.getAllPartitions();
        getReadLock().lock();
        try {
            Collection<IndexPartition> partitions2 = index.getAllPartitions();
            return concat(partitions1, partitions2);
        } finally {
            getReadLock().unlock();
        }
    }

    public Collection<IndexPartitionDescriptor> getAllPartitionDescriptors() {
        // TODO: No duplicates?
        Collection<IndexPartitionDescriptor> partitions1 = transactionIndex.getAllPartitionDescriptors();
        getReadLock().lock();
        try {
            Collection<IndexPartitionDescriptor> partitions2 = index.getAllPartitionDescriptors();
            return concat(partitions1, partitions2);
        } finally {
            getReadLock().unlock();
        }
    }

    public void clear() {
        // Should not be called on transaction index, index cannot be cleared.
        assert false;
    }

    public IStrategoTerm toTerm(boolean includePositions) {
        return index.toTerm(includePositions);
    }

    /**
     * Queries if given partition descriptor equals the current partition; the partition this transaction index has been
     * created for.
     * 
     * @param partitionDescriptor The partition descriptor to check.
     * @return True if given partition descriptor equals the current partition.
     */
    private boolean isCurrentPartition(IndexPartitionDescriptor partitionDescriptor) {
        return partitionDescriptor.equals(currentPartition)
            || partitionDescriptor.getURI().equals(currentPartition.getURI());
    }

    /**
     * Query if given entry should be visible. Entries are invisible if the current partition is cleared and the
     * partition descriptor of the entry equals the current partition descriptor. Invisible entries from the global
     * index should not be returned.
     * 
     * @param entry The entry to check.
     * @return True if given entry should be visible, false otherwise.
     */
    private boolean isEntryVisible(IndexEntry entry) {
        return !(clearedCurrentPartition && isCurrentPartition(entry.getPartitionDescriptor()));
    }

    /**
     * Given a collection of entries, filters out all invisible entries.
     * 
     * @see #isEntryVisible
     * @param entries The collection of entries to filter.
     * @return Filtered collection of entries.
     */
    private Collection<IndexEntry> filterInvisibleEntries(Collection<IndexEntry> entries) {
        if(!clearedCurrentPartition)
            return entries;

        List<IndexEntry> l = new ArrayList<IndexEntry>(entries.size());
        for(IndexEntry entry : entries) {
            if(isEntryVisible(entry))
                l.add(entry);
        }
        return l;
    }

    private <T> Collection<T> concat(Collection<T> c1, Collection<T> c2) {
        // Need to copy here, collections could be changed from other threads.
        List<T> l = new ArrayList<T>(c1.size() + c2.size());
        l.addAll(c1);
        l.addAll(c2);

        return l;
    }

    private Lock getReadLock() {
        return IndexManager.getTransactionLock().readLock();
    }

    private Lock getWriteLock() {
        return IndexManager.getTransactionLock().writeLock();
    }
}
