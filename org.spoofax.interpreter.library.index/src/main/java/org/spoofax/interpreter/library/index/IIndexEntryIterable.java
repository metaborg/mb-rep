package org.spoofax.interpreter.library.index;

/**
 * Interface for an iterable of {@link IndexEntry}.
 * 
 * @author Gabriel
 */
public interface IIndexEntryIterable extends Iterable<IndexEntry> {
    /**
     * @return Array representation of iterable.
     */
    public IndexEntry[] toArray();
    
    /**
     * Locks all locks that belong to this iterable. Call before iterating.
     */
    public void lock();

    /**
     * Unlocks all locks that belong to this iterable. Call after iterating.
     */
    public void unlock();
}
