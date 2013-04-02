package org.spoofax.interpreter.library.index;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Gabriël Konat
 */
public class TransactionIndex implements IIndex {
	private IIndex index;
	private IIndex transactionIndex;
	private IndexPartitionDescriptor currentPartition;
	private boolean clearedCurrentPartition = false;
	private List<TemplateWithPartitionDescriptor> removedEntries = new ArrayList<TemplateWithPartitionDescriptor>();
	private List<IStrategoAppl> removedAllEntries = new ArrayList<IStrategoAppl>();

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

	public Collection<IStrategoAppl> getRemovedAllEntries() {
		return removedAllEntries;
	}

	public void initialize(ITermFactory factory, IOAgent agent) {
		// Should not be called, both the index and transaction index should already be initialized.
		assert false;
	}

	public IndexEntryFactory getFactory() {
		return index.getFactory();
	}

	public void startCollection(IndexPartitionDescriptor partitionDescriptor) {
		transactionIndex.startCollection(partitionDescriptor);
	}

	public IStrategoTuple stopCollection() {
		return transactionIndex.stopCollection();
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

	public Collection<IndexEntry> remove(IStrategoAppl template, IndexPartitionDescriptor partitionDescriptor) {
		Collection<IndexEntry> removed = transactionIndex.remove(template, partitionDescriptor);
		removedEntries.add(new TemplateWithPartitionDescriptor(template, partitionDescriptor));
		return removed;
	}

	public Collection<IndexEntry> removeAll(IStrategoAppl template) {
		Collection<IndexEntry> removed = transactionIndex.removeAll(template);
		removedAllEntries.add(template);
		return removed;
	}

	public Collection<IndexEntry> removeOne(IStrategoAppl entryTerm) {
		Collection<IndexEntry> removed = transactionIndex.removeOne(entryTerm);
		removedAllEntries.add(entryTerm);
		return removed;
	}

	public IIndexEntryIterable get(final IStrategoAppl template) {
		return new AbstractIndexEntryIterable(getReadLock()) {
			@Override
			public Iterator<IndexEntry> iterator() {
				return new TransactionIndexEntryIterator(clearedCurrentPartition, currentPartition, transactionIndex
					.get(template).iterator(), index.get(template).iterator());
			}
		};
	}

	public IIndexEntryIterable getAll() {
		return new AbstractIndexEntryIterable(getReadLock()) {
			@Override
			public Iterator<IndexEntry> iterator() {
				return new TransactionIndexEntryIterator(clearedCurrentPartition, currentPartition, transactionIndex
					.getAll().iterator(), index.getAll().iterator());
			}
		};
	}

	public IIndexEntryIterable getChildren(final IStrategoAppl template) {
		return new AbstractIndexEntryIterable(getReadLock()) {
			@Override
			public Iterator<IndexEntry> iterator() {
				return new TransactionIndexEntryIterator(clearedCurrentPartition, currentPartition, transactionIndex
					.getChildren(template).iterator(), index.getChildren(template).iterator());
			}
		};
	}

	public IIndexEntryIterable getInPartition(final IndexPartitionDescriptor partitionDescriptor) {
		if(isCurrentPartition(partitionDescriptor) && clearedCurrentPartition)
			// Current partition has been cleared, entries from index should not be visible.
			return transactionIndex.getInPartition(partitionDescriptor);

		return new AbstractIndexEntryIterable(getReadLock()) {
			@Override
			public Iterator<IndexEntry> iterator() {
				return new TransactionIndexEntryIterator(clearedCurrentPartition, currentPartition, transactionIndex
					.getInPartition(partitionDescriptor).iterator(), index.getInPartition(partitionDescriptor)
					.iterator());
			}
		};
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

	public void clearPartition(IStrategoTerm partitionTerm) {
		clearPartition(transactionIndex.getPartitionDescriptor(partitionTerm));
	}

	public void clearPartition(IndexPartitionDescriptor partitionDescriptor) {
		assert isCurrentPartition(partitionDescriptor); // May only clear current partition.

		clearedCurrentPartition = true;
		transactionIndex.clearPartition(partitionDescriptor);
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

	public void clearAll() {
		transactionIndex.clearAll();

		getWriteLock().lock();
		index.clearAll();
		getWriteLock().unlock();
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
