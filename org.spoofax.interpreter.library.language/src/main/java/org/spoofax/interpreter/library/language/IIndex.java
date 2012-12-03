package org.spoofax.interpreter.library.language;

import java.util.Collection;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Gabriël Konat
 */
public interface IIndex {
    /**
     * Initializes this index.
     */
    public abstract void initialize(ITermFactory factory, IOAgent agent);

    /**
     * Gets the entry factory used by this index.
     */
    public abstract IndexEntryFactory getFactory();

    /**
     * Adds a new entry to the index.
     * 
     * @param entry The entry to add.
     * @param partitionDescriptor The partition to associate the entry with.
     */
    public abstract void add(IStrategoAppl entry, IndexPartitionDescriptor partitionDescriptor);

    /**
     * Adds a new entry to the index.
     * 
     * @param entry The entry to add.
     */
    public abstract void add(IndexEntry entry);

    /**
     * Adds a list of entries to the index.
     * 
     * @param entries The entries to add.
     * @param partitionDescriptor The partition to associate the entries with.
     */
    public abstract void addAll(IStrategoList entries, IndexPartitionDescriptor partitionDescriptor);

    /**
     * Removes all entries that match given template and are from given partition. 
     * Warning: VERY SLOW!
     * 
     * @param template The template to match entries against.
     * @param partitionDescriptor The partition entries will be removed from.
     */
    public abstract void remove(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor);

    /**
     * Gets all entries that match given template.
     * 
     * @param template The template to match entries against.
     */
    public abstract Collection<IndexEntry> getEntries(IStrategoAppl template);

    /**
     * Gets all entries.
     */
    public abstract Collection<IndexEntry> getAllEntries();

    /**
     * Gets all child entries for URI in given template.
     * 
     * @param template The template to match entries against.
     */
    public abstract Collection<IndexEntry> getEntryChildTerms(IStrategoAppl template);

    /**
     * Gets all entries for given partition descriptor.
     * 
     * @param partitionDescriptor The partition descriptor to match entries against.
     */
    public abstract Collection<IndexEntry> getEntriesInPartition(IndexPartitionDescriptor partitionDescriptor);

    /**
     * Gets all partitions that contain entries that match given template. Returned collection is a set of partitions,
     * it does not contain duplicates.
     * 
     * @param template The template to match entries against.
     */
    public abstract Collection<IndexPartitionDescriptor> getPartitionsOf(IStrategoAppl template);

    /**
     * Gets an index partition for given partition descriptor.
     * 
     * @param partitionDescriptor A partition descriptor.
     */
    public abstract IndexPartition getPartition(IndexPartitionDescriptor partitionDescriptor);

    /**
     * Gets an index partition descriptor for given partition term.
     * 
     * @param partitionTerm A string or (string, string) tuple with a file name or the file name and partition
     *            identifier.
     */
    public abstract IndexPartitionDescriptor getPartitionDescriptor(IStrategoTerm partitionTerm);

    /**
     * Removes all entries in given partition term and removes the partition itself.
     * 
     * @param partitionTerm A string or (string, string) tuple with a file name or the file name and partition
     *            identifier.
     */
    public abstract void removePartition(IStrategoTerm partitionTerm);

    /**
     * Removes all entries for given partition and removes the partition itself.
     * 
     * @param partitionDescriptor A partition descriptor.
     */
    public abstract void removePartition(IndexPartitionDescriptor partitionDescriptor);

    /**
     * Gets all partitions that are in the index.
     */
    public abstract Collection<IndexPartition> getAllPartitions();

    /**
     * Gets all partition descriptors that are in the index.
     */
    public abstract Collection<IndexPartitionDescriptor> getAllPartitionDescriptors();

    /**
     * Clears the entire index.
     */
    public abstract void clear();

    /**
     * Returns the index as a Stratego term.
     * 
     * @param includePositions
     */
    public abstract IStrategoTerm toTerm(boolean includePositions);

    public abstract String toString();
}
