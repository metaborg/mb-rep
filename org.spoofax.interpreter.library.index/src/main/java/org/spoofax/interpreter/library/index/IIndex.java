package org.spoofax.interpreter.library.index;

import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTuple;

public interface IIndex {

	/**
	 * Gets the entry factory used by this index.
	 */
	public abstract IndexEntryFactory getFactory();

	/**
	 * Returns the parent index or null if it does not have a parent.
	 */
	public abstract IIndex getParent();
	
	/**
	 * Starts collection for given partition.
	 * 
	 * @param partition
	 */
	public abstract void startCollection(IndexPartition partition);

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
	 */
	public abstract void add(IndexEntry entry);

	/**
	 * Gets all entries that match given template.
	 * 
	 * @param template The template to match entries against.
	 */
	public abstract Iterable<IndexEntry> get(IStrategoAppl template);

	/**
	 * Gets all entries.
	 */
	public abstract Iterable<IndexEntry> getAll();
	
	/**
	 * Gets all entries, excluding entries from the parent index.
	 */
	public abstract Iterable<IndexEntry> getAllCurrent();

	/**
	 * Gets all child entries for URI in given template.
	 * 
	 * @param template The template to match entries against.
	 */
	public abstract Iterable<IndexEntry> getChildren(IStrategoAppl template);

	/**
	 * Gets all entries for given partition descriptor.
	 * 
	 * @param partition The partition descriptor to match entries against.
	 */
	public abstract Iterable<IndexEntry> getInPartition(IndexPartition partition);

	/**
	 * Gets all partitions that contain entries that match given template. Returned collection is a set of partitions,
	 * it does not contain duplicates.
	 * 
	 * @param template The template to match entries against.
	 */
	public abstract Set<IndexPartition> getPartitionsOf(IStrategoAppl template);

	/**
	 * Gets all partitions that are in the index.
	 */
	public abstract Iterable<IndexPartition> getAllPartitions();
	
	/**
	 * Gets partitions that have been cleared.
	 */
	public abstract Iterable<IndexPartition> getClearedPartitions();

	/**
	 * Removes all entries for given partition and removes the partition itself.
	 * 
	 * @param partition A partition descriptor.
	 */
	public abstract void clearPartition(IndexPartition partition);

	/**
	 * Resets the index to the initial state.
	 */
	public abstract void reset();
}
