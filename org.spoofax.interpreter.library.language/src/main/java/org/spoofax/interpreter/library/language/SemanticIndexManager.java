package org.spoofax.interpreter.library.language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.io.binary.TermReader;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexManager {
	
	private final static AtomicLong revisionProvider = new AtomicLong();
	
	private final static ReadWriteLock transactionLock = new ReentrantReadWriteLock();

	private ThreadLocal<ISemanticIndex> current = new ThreadLocal<ISemanticIndex>();
	
	private ThreadLocal<URI> currentProject = new ThreadLocal<URI>();
	
	private ThreadLocal<String> currentLanguage = new ThreadLocal<String>();
	
	private ThreadLocal<SemanticIndexFileDescriptor> currentFile = new ThreadLocal<SemanticIndexFileDescriptor>();
	
	/**
	 * Indices by language and project. Access requires a lock on {@link #getSyncRoot}
	 */
	private static Map<String, Map<URI, WeakReference<ISemanticIndex>>> asyncIndexCache =
		new HashMap<String, Map<URI, WeakReference<ISemanticIndex>>>();
	
	public static ReadWriteLock getTransactionLock() {
		return transactionLock;
	}
	
	public ISemanticIndex getCurrent() {
		if (!isInitialized())
			throw new IllegalStateException("No semantic index has been set-up, use index-setup(|language, project-paths) to set up the index before use.");
		
		return current.get();
	}
	
	public SemanticIndexFileDescriptor getCurrentFile() {
		if (!isInitialized())
			throw new IllegalStateException("No semantic index has been set-up, use index-setup(|language, project-paths) to set up the index before use.");
		
		return currentFile.get();
	}
	
	public void setCurrentFile(SemanticIndexFileDescriptor currentFile) {
		this.currentFile.set(currentFile);
	}

	public URI getCurrentProject() {
		if (!isInitialized())
			throw new IllegalStateException("No semantic index has been set-up, use index-setup(|language, project-paths) to set up the index before use.");
		
		return currentProject.get();
	}
	
	public long startTransaction(ITermFactory factory, IOAgent agent) {
		// TODO: Does this operation need a transaction write lock?
		
		long rev = revisionProvider.getAndIncrement();
		ISemanticIndex currentIndex = current.get();
		currentIndex.getFile(currentFile.get()).setTimeRevision(new Date(), rev);
		
		assert currentIndex instanceof SemanticIndex; // Prevent multiple transactions.
		
		ISemanticIndex transactionIndex = new SemanticIndex();
		transactionIndex.initialize(factory, agent);
		current.set(new TransactionSemanticIndex(currentIndex, transactionIndex, currentFile.get()));
		
		return rev;
	}
	
	public void endTransaction() {
		TransactionSemanticIndex currentIndex = (TransactionSemanticIndex)current.get();
		ISemanticIndex index = currentIndex.getIndex();
		ISemanticIndex transactionIndex = currentIndex.getTransactionIndex();
		current.set(index);
		
		transactionLock.writeLock().lock();
		try {
			// TODO: Efficient copy of transactionIndex into index.
			
			if(currentIndex.hasClearedCurrentFile())
				index.removeFile(currentIndex.getCurrentFile());
			
			for(TemplateWithFileDescriptor entry : currentIndex.getRemovedEntries())
				index.remove(entry.getTemplate(), entry.getFileDescriptor());
			
			for(SemanticIndexEntry entry : transactionIndex.getAllEntries())
				index.add(entry);
		} finally {
			transactionLock.writeLock().unlock();
		}
	}
	
	private static Object getSyncRoot() {
		return SemanticIndexManager.class;
	}
	
	public AtomicLong getRevisionProvider() {
		return revisionProvider;
	}
	
	public boolean isInitialized() {
		return current.get() != null;
	}
	
	public static boolean isKnownIndexingLanguage(String language) {
		synchronized (getSyncRoot()) {
			return asyncIndexCache.containsKey(language);
		}
	}
	
	public void loadIndex(String language, URI project, ITermFactory factory, IOAgent agent) {
		synchronized (getSyncRoot()) {
			Map<URI, WeakReference<ISemanticIndex>> indicesByProject =
					asyncIndexCache.get(language);
			if (indicesByProject == null) {
				indicesByProject = new HashMap<URI, WeakReference<ISemanticIndex>>();
				asyncIndexCache.put(language, indicesByProject);
			}
			WeakReference<ISemanticIndex> indexRef = indicesByProject.get(project);
			ISemanticIndex index = indexRef == null ? null : indexRef.get();
			if (index == null) {
				index = tryReadFromFile(getIndexFile(project, language), factory, agent);
			}
			if (index == null) {
				index = new SemanticIndex();
				NotificationCenter.notifyNewProject(project);
			}
			indicesByProject.put(project, new WeakReference<ISemanticIndex>(index));
			current.set(index);
			currentLanguage.set(language);
			currentProject.set(project);
		}
	}
	
	public ISemanticIndex tryReadFromFile(File file, ITermFactory factory, IOAgent agent) {
		try {
			IStrategoTerm term = new TermReader(factory).parseFromFile(file.toString());
			return SemanticIndex.fromTerm(term, factory, agent, true); // TODO: Move to other class
		} catch (IOException e) {
			return null;
		}
	}
	
	public void storeCurrent() throws IOException {
		File file = getIndexFile(currentProject.get(), currentLanguage.get());
		IStrategoTerm stored = getCurrent().toTerm(true);
		Writer writer = new BufferedWriter(new FileWriter(file));
		try {
			stored.writeAsString(writer, IStrategoTerm.INFINITE);
		} finally {
			writer.close();
		}
	}

	private File getIndexFile(URI project, String language) {
		File container = new File(new File(project), ".cache");
		container.mkdirs();
		return new File(container, language + ".idx");
	}
}
