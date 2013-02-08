package org.spoofax.interpreter.library.index;

import java.util.Iterator;

/**
 * Iterator for index entries from a {@link TransactionIndex}.
 * 
 * @author Gabriel
 */
public class TransactionIndexEntryIterator implements Iterator<IndexEntry> {
    private boolean clearedCurrentPartition;
    private IndexPartitionDescriptor currentPartition;
    private Iterator<IndexEntry> transactionIndexEntries;
    private Iterator<IndexEntry> indexEntries;
    private IndexEntry nextEntry = null;

    public TransactionIndexEntryIterator(boolean clearedCurrentPartition, IndexPartitionDescriptor currentPartition,
        Iterator<IndexEntry> transactionIndexEntries, Iterator<IndexEntry> indexEntries) {
        this.clearedCurrentPartition = clearedCurrentPartition;
        this.currentPartition = currentPartition;
        this.transactionIndexEntries = transactionIndexEntries;
        this.indexEntries = indexEntries;
    }

    public boolean hasNext() {
        IndexEntry next = nextEntry();
        if(next == null) {
            return false;
        } else {
            nextEntry = next;
            return true;
        }
    }

    public IndexEntry next() {
        if(nextEntry == null)
            return nextEntry();
        else
            return nextEntry;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private IndexEntry nextEntry() {
        if(transactionIndexEntries.hasNext()) {
            return transactionIndexEntries.next();
        } else if(indexEntries.hasNext()) {
            IndexEntry next = indexEntries.next();
            if(isEntryVisible(next))
                return next;
            else
                return nextEntry();
        } else {
            return null;
        }
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
        return !(clearedCurrentPartition && isCurrentPartition(entry.getPartition()));
    }
}
