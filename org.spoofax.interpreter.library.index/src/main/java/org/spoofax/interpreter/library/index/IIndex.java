package org.spoofax.interpreter.library.index;

import java.util.Collection;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
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
	 * Starts collection for given partition.
	 * 
	 * @param partitionDescriptor
	 */
	public abstract void startCollection(IndexPartitionDescriptor partitionDescriptor);

	/**
	 * Stops collection for given partition, returning the entries that were removed and added during collection.
	 * 
	 * @return The removed and added entries as a Stratego tuple.
	 */
	public abstract IStrategoTuple stopCollection();

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
	 * Removes all entries that match given template and are from given partition. Warning: VERY SLOW!
	 * 
	 * @param template The template to match entries against.
	 * @param partitionDescriptor The partition entries will be removed from.
	 */
	public abstract Collection<IndexEntry> remove(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor);

	/**
	 * Removes all entries that match given template (from all partitions). Warning: Quite slow!
	 * 
	 * @param template The template to match entries against.
	 */
	public abstract Collection<IndexEntry> removeAll(IStrategoAppl template);

	/**
	 * Removes one given entry. Compares both the URI and value of the entry. Warning: VERY SLOW!
	 * 
	 * @param entryTerm The term representing the entry to remove.
	 */
	public abstract Collection<IndexEntry> removeOne(IStrategoAppl entryTerm);

	/**
	 * Gets all entries that match given template.
	 * 
	 * @param template The template to match entries against.
	 */
	public abstract IIndexEntryIterable get(IStrategoAppl template);

	/**
	 * Gets all entries.
	 */
	public abstract IIndexEntryIterable getAll();

	/**
	 * Gets all child entries for URI in given template.
	 * 
	 * @param template The template to match entries against.
	 */
	public abstract IIndexEntryIterable getChildren(IStrategoAppl template);

	/**
	 * Gets all entries for given partition descriptor.
	 * 
	 * @param partitionDescriptor The partition descriptor to match entries against.
	 */
	public abstract IIndexEntryIterable getInPartition(IndexPartitionDescriptor partitionDescriptor);

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
	 * Gets all partitions that are in the index.
	 */
	public abstract Collection<IndexPartition> getAllPartitions();

	/**
	 * Gets all partition descriptors that are in the index.
	 */
	public abstract Collection<IndexPartitionDescriptor> getAllPartitionDescriptors();

	/**
	 * Removes all entries in given partition term and removes the partition itself.
	 * 
	 * @param partitionTerm A string or (string, string) tuple with a file name or the file name and partition
	 *            identifier.
	 */
	public abstract void clearPartition(IStrategoTerm partitionTerm);

	/**
	 * Removes all entries for given partition and removes the partition itself.
	 * 
	 * @param partitionDescriptor A partition descriptor.
	 */
	public abstract void clearPartition(IndexPartitionDescriptor partitionDescriptor);

	/**
	 * Clears the entire index.
	 */
	public abstract void clearAll();
}
