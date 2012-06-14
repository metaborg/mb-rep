package org.spoofax.interpreter.library.language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.locks.Lock;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class TransactionSemanticIndex implements ISemanticIndex {
	private ISemanticIndex index;
	private ISemanticIndex transactionIndex;
	private SemanticIndexFileDescriptor currentFile;
	private boolean clearedCurrentFile = false;
	private List<TemplateWithFileDescriptor> removedEntries = new ArrayList<TemplateWithFileDescriptor>();
	
	public TransactionSemanticIndex(ISemanticIndex index, ISemanticIndex transactionIndex, 
			SemanticIndexFileDescriptor currentFile) {
		this.index = index;
		this.transactionIndex = transactionIndex;
		this.currentFile = currentFile;
	}
	
	public ISemanticIndex getIndex() {
		return index;
	}
	
	public ISemanticIndex getTransactionIndex() {
		return transactionIndex;
	}
	
	public boolean hasClearedCurrentFile() {
		return clearedCurrentFile;
	}
	
	public SemanticIndexFileDescriptor getCurrentFile() {
		return currentFile;
	}
	
	public Collection<TemplateWithFileDescriptor> getRemovedEntries() {
		return removedEntries;
	}
	
	public void initialize(ITermFactory factory, IOAgent agent) {
		// Should not be called, both the index and transaction index should already be initialized.
		assert false;
	}

	public SemanticIndexEntryFactory getFactory() {
		return index.getFactory();
	}

	public void add(IStrategoAppl entry, SemanticIndexFileDescriptor fileDescriptor) {
		transactionIndex.add(entry, fileDescriptor);
	}
	
	public void add(SemanticIndexEntry entry) {
		transactionIndex.add(entry);
	}

	public void addAll(IStrategoList entries, SemanticIndexFileDescriptor fileDescriptor) {
		transactionIndex.addAll(entries, fileDescriptor);
	}
	
	public void remove(IStrategoAppl template, SemanticIndexFileDescriptor fileDescriptor) {
		transactionIndex.remove(template, fileDescriptor);
		removedEntries.add(new TemplateWithFileDescriptor(template, fileDescriptor));
	}

	public Collection<SemanticIndexEntry> getEntries(IStrategoAppl template) {
		Collection<SemanticIndexEntry> entries1 = transactionIndex.getEntries(template);
		getReadLock().lock();
		try {
			Collection<SemanticIndexEntry> entries2 = filterInvisibleEntries(index.getEntries(template));
			return concat(entries1, entries2);
		} finally {
			getReadLock().unlock();
		}
	}
	
	public Collection<SemanticIndexEntry> getAllEntries() {
		Collection<SemanticIndexEntry> entries1 = transactionIndex.getAllEntries();
		getReadLock().lock();
		try {
			Collection<SemanticIndexEntry> entries2 = filterInvisibleEntries(index.getAllEntries());
			return concat(entries1, entries2);
		} finally {
			getReadLock().unlock();
		}
	}

	public Collection<SemanticIndexEntry> getEntryChildTerms(
			IStrategoAppl template) {
		Collection<SemanticIndexEntry> entries1 = transactionIndex.getEntryChildTerms(template);
		getReadLock().lock();
		try {
			Collection<SemanticIndexEntry> entries2 = filterInvisibleEntries(index.getEntryChildTerms(template));
			return concat(entries1, entries2);
		} finally {
			getReadLock().unlock();
		}
	}

	public Collection<SemanticIndexEntry> getEntriesInFile(
			SemanticIndexFileDescriptor fileDescriptor) {
		Collection<SemanticIndexEntry> entries1 = transactionIndex.getEntriesInFile(fileDescriptor);
		
		if(isCurrentFile(fileDescriptor) && clearedCurrentFile)
			return entries1; // Current file has been cleared, entries from index should not be visible.
		
		getReadLock().lock();
		try {
			Collection<SemanticIndexEntry> entries2 = index.getEntriesInFile(fileDescriptor);
			return concat(entries1, entries2);
		} finally {
			getReadLock().unlock();
		}
	}
	
	public SemanticIndexFile getFile(SemanticIndexFileDescriptor fileDescriptor) {
		// Need a write lock here because getFile can add a new file.
		getWriteLock().lock();
		try {
			return index.getFile(fileDescriptor);
		} finally {
			getWriteLock().unlock();
		}
	}
	
	public SemanticIndexFileDescriptor getFileDescriptor(IStrategoTerm fileTerm) {
		return index.getFileDescriptor(fileTerm);
	}

	public void removeFile(IStrategoTerm fileTerm) {
		removeFile(transactionIndex.getFileDescriptor(fileTerm));
	}
	
	public void removeFile(SemanticIndexFileDescriptor fileDescriptor) {
		assert isCurrentFile(fileDescriptor); // May only clear current file.

		clearedCurrentFile = true;
		transactionIndex.removeFile(fileDescriptor);
	}

	public Collection<SemanticIndexFile> getAllFiles() {
		// TODO: No duplicates?
		Collection<SemanticIndexFile> files1 = transactionIndex.getAllFiles();
		getReadLock().lock();
		try {
			Collection<SemanticIndexFile> files2 = index.getAllFiles();
			return concat(files1, files2);
		} finally {
			getReadLock().unlock();
		}
	}
	
	public Collection<SemanticIndexFileDescriptor> getAllFileDescriptors() {
		// TODO: No duplicates?
		Collection<SemanticIndexFileDescriptor> files1 = transactionIndex.getAllFileDescriptors();
		getReadLock().lock();
		try {
			Collection<SemanticIndexFileDescriptor> files2 = index.getAllFileDescriptors();
			return concat(files1, files2);
		} finally {
			getReadLock().unlock();
		}
	}

	public void clear() {
		// Should not be called on transaction index, index cannot be cleared.
		assert false;
	}

	public IStrategoTerm toTerm(boolean includePositions) {
		return index.toTerm(includePositions);
	}
	
	/**
	 * Queries if given file descriptor equals the current file; the file this transaction index has 
	 * been created for.
	 * 
	 * @param fileDescriptor	The file descriptor to check.
	 * @return True if given file descriptor equals the current file.
	 */
	private boolean isCurrentFile(SemanticIndexFileDescriptor fileDescriptor) {
		return fileDescriptor.equals(currentFile) || fileDescriptor.getURI().equals(currentFile.getURI());
	}
	
	/**
	 * Query if given entry should be visible. Entries are invisible if the current file is cleared and the
	 * file descriptor of the entry equals the current file descriptor. Invisible entries from the global
	 * index should not be returned.
	 * 
	 * @param entry	The entry to check.
	 * @return True if given entry should be visible, false otherwise.
	 */
	private boolean isEntryVisible(SemanticIndexEntry entry) {
		return !(clearedCurrentFile && isCurrentFile(entry.getFileDescriptor()));
	}
	
	/**
	 * Given a collection of entries, filters out all invisible entries.
	 * 
	 * @see #isEntryVisible
	 * @param entries The collection of entries to filter.
	 * @return Filtered collection of entries.
	 */
	private Collection<SemanticIndexEntry> filterInvisibleEntries(Collection<SemanticIndexEntry> entries) {
		if(!clearedCurrentFile)
			return entries;
		
		List<SemanticIndexEntry> l = new ArrayList<SemanticIndexEntry>(entries.size());
		for(SemanticIndexEntry entry : entries) {
			if(isEntryVisible(entry))
				l.add(entry);
		}
		return l;
	}
	
	private <T> Collection<T> concat(Collection<T> c1, Collection<T> c2) {
		// Need to copy here, collections could be changed from other threads.
		List<T> l = new ArrayList<T>(c1.size() + c2.size());
		l.addAll(c1);
		l.addAll(c2);
		
		return l;
	}
	
	private Lock getReadLock() {
		return SemanticIndexManager.getTransactionLock().readLock();
	}
	
	private Lock getWriteLock() {
		return SemanticIndexManager.getTransactionLock().writeLock();
	}
}
