package org.spoofax.interpreter.library.index;

public interface IHierarchicalIndex extends IIndex {
	/**
	 * Returns the parent index or null if it does not have a parent.
	 */
	public abstract IIndex getParent();
	
	/**
	 * Gets all entries, excluding entries from the parent index.
	 */
	public abstract Iterable<IndexEntry> getAllCurrent();
	
	/**
	 * Gets partitions that have been cleared.
	 */
	public abstract Iterable<IndexPartition> getClearedPartitions();
}
