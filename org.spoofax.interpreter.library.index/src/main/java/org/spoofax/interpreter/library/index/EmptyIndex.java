package org.spoofax.interpreter.library.index;

import java.util.Set;

import org.spoofax.interpreter.library.index.util.EmptyIterable;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.google.common.collect.ImmutableSet;

/**
 * Stub index that is always empty. Used as the parent index for the root index.
 */
public class EmptyIndex implements IIndex {

	public IndexEntryFactory getFactory() {
		return null;
	}

	public IIndex getParent() {
		return null;
	}

	public void startCollection(IndexPartition partition) {

	}

	public IStrategoTuple stopCollection() {
		return null;
	}

	public void add(IndexEntry entry) {

	}

	public Iterable<IndexEntry> get(IStrategoAppl template) {
		return new EmptyIterable<IndexEntry>();
	}

	public Iterable<IndexEntry> getAll() {
		return new EmptyIterable<IndexEntry>();
	}
	
	public Iterable<IndexEntry> getAllCurrent() {
		return new EmptyIterable<IndexEntry>();
	}

	public Iterable<IndexEntry> getChildren(IStrategoAppl template) {
		return new EmptyIterable<IndexEntry>();
	}

	public Iterable<IndexEntry> getInPartition(IndexPartition partition) {
		return new EmptyIterable<IndexEntry>();
	}

	public Set<IndexPartition> getPartitionsOf(IStrategoAppl template) {
		return ImmutableSet.of();
	}

	public Iterable<IndexPartition> getAllPartitions() {
		return new EmptyIterable<IndexPartition>();
	}
	
	public Iterable<IndexPartition> getClearedPartitions() {
		return new EmptyIterable<IndexPartition>();
	}

	public void clearPartition(IndexPartition partition) {

	}

	public void clearAll() {

	}
}
