package org.spoofax.interpreter.library.index;

import java.io.Serializable;
import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.jsglr.client.imploder.ImploderAttachment;

/**
 * A key-value pair that can be stored in an {@link IIndex}, partitioned by a {@link IndexPartition}.
 */
public class IndexEntry implements Serializable {
	private static final long serialVersionUID = -1073077973341978805L;

	private final IndexURI key;
	private final IStrategoTerm value;
	private final IndexPartition partition;

	private transient IStrategoAppl cachedTerm;

	/**
	 * Use {@link IndexEntryFactory#createEntry}.
	 */
	protected IndexEntry(IndexURI key, IStrategoTerm value, IndexPartition partition) {
		this.key = key;
		this.value = value;
		this.partition = partition;
	}

	public IndexURI getKey() {
		return key;
	}

	public IStrategoTerm getValue() {
		return value;
	}

	public IndexPartition getPartition() {
		return partition;
	}

	/**
	 * Returns the term representation.
	 */
	public IStrategoAppl toTerm(ITermFactory factory) {
		if(cachedTerm != null)
			return cachedTerm;

		cachedTerm = key.toTerm(factory, value);

		return forceImploderAttachment(cachedTerm);
	}

	/**
	 * Returns a list with term representations of given entries.
	 */
	public static IStrategoList toTerms(ITermFactory factory, Collection<IndexEntry> entries) {
		IStrategoList results = factory.makeList();
		for(IndexEntry entry : entries) {
			results = factory.makeListCons(entry.toTerm(factory), results);
		}
		return results;
	}

	/**
	 * Returns a list with term representations of given entries.
	 */
	public static IStrategoList toTerms(ITermFactory factory, Iterable<IndexEntry> entries) {
		IStrategoList results = factory.makeList();
		for(IndexEntry entry : entries) {
			results = factory.makeListCons(entry.toTerm(factory), results);
		}
		return results;
	}

	/**
	 * Returns a list with tuples of term representations of given entries with their partitions.
	 */
	public static IStrategoList toTermsWithPartition(ITermFactory factory, Iterable<IndexEntry> entries) {
		IStrategoList results = factory.makeList();
		for(IndexEntry entry : entries) {
			results =
				factory.makeListCons(factory.makeTuple(entry.getPartition().toTerm(factory), entry.toTerm(factory)),
					results);
		}
		return results;
	}

	/**
	 * Force an imploder attachment for a term. This ensures that there is always some form of position info, and makes
	 * sure that origin info is not added to the term. (The latter would be bad since we cache in {@link #cachedTerm}.)
	 */
	private IStrategoAppl forceImploderAttachment(IStrategoAppl term) {
		ImploderAttachment attach = ImploderAttachment.get(key.getIdentifier());
		if(attach != null) {
			ImploderAttachment.putImploderAttachment(term, false, attach.getSort(), attach.getLeftToken(),
				attach.getRightToken());
		} else {
			String fn = partition == null ? null : partition.getURI().getPath();
			attach = ImploderAttachment.createCompactPositionAttachment(fn, 0, 0, 0, -1);
			term.putAttachment(attach);
		}
		return term;
	}

	@Override
	public String toString() {
		String result = key.toString();
		if(value != null)
			result += "," + value;
		return result + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		result = prime * result + ((partition == null) ? 0 : partition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(!(obj instanceof IndexEntry))
			return false;

		IndexEntry other = (IndexEntry) obj;

		if(key == null) {
			if(other.key != null)
				return false;
		} else if(!key.equals(other.key))
			return false;

		if(value == null) {
			if(other.value != null)
				return false;
		} else if(!value.equals(other.value))
			return false;

		if(partition == null) {
			if(other.partition != null)
				return false;
		} else if(!partition.equals(other.partition))
			return false;

		return true;
	}
}
