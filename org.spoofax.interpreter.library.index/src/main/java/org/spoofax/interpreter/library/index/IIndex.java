package org.spoofax.interpreter.library.index;

import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public interface IIndex {
    /**
     * Gets the entry factory.
     */
    public abstract IndexEntryFactory entryFactory();


    /**
     * Starts collection for given source.
     */
    public abstract void startCollection(IStrategoTerm source);

    /**
     * Collects a new index entry, which will be added when collection is stopped.
     */
    public abstract IndexEntry collect(IStrategoTerm key, IStrategoTerm value);

    /**
     * Collects a new index entry, which will be added when collection is stopped.
     */
    public abstract IndexEntry collect(IStrategoTerm key);

    /**
     * Stops collection, returning the entries that were removed and added during collection.
     *
     * @return The removed and added entries as a tuple.
     */
    public abstract IStrategoTuple stopCollection(IStrategoTerm source);


    /**
     * Adds a new entry to the index.
     */
    public abstract void add(IndexEntry entry);

    public abstract void addAll(IStrategoTerm source, Iterable<IndexEntry> entry);


    /**
     * Gets all entries that match given template.
     */
    public abstract Iterable<IndexEntry> get(IStrategoTerm key);

    /**
     * Gets all child entries for URI in given template.
     */
    public abstract Iterable<IndexEntry> getChilds(IStrategoTerm key);

    /**
     * Gets all entries.
     */
    public abstract Iterable<IndexEntry> getAll();


    /**
     * Gets all entries for given source
     */
    public abstract Iterable<IndexEntry> getInSource(IStrategoTerm source);

    /**
     * Gets all sources that contain given key. Returned collection is a set of partitions, it does not contain
     * duplicates.
     */
    public abstract Set<IStrategoTerm> getSourcesOf(IStrategoTerm key);

    /**
     * Gets all sources.
     */
    public abstract Iterable<IStrategoTerm> getAllSources();

    /**
     * Removes all entries for given source and removes the source itself.
     */
    public abstract void clearSource(IStrategoTerm source);


    /**
     * Attempt to recover the index after an exception during collection.
     */
    public abstract void recover();

    /**
     * Resets the index to the initial state.
     */
    public abstract void reset();
}
