package org.spoofax.interpreter.library.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.io.binary.SAFWriter;
import org.spoofax.terms.io.binary.TermReader;

public class IndexManager {
	private static final IndexManager INSTANCE = new IndexManager();
	private static final AtomicLong revisionProvider = new AtomicLong();
	private static final ReadWriteLock transactionLock = new ReentrantReadWriteLock();
	private static final IndexFactory indexFactory = new IndexFactory();

	/**
	 * Indices by language and project. Access requires a lock on {@link #getSyncRoot}
	 */
	private static Map<URI, WeakReference<IIndex>> indexCache = new HashMap<URI, WeakReference<IIndex>>();
	private static Set<String> indexedLanguages = new HashSet<String>();

	private ThreadLocal<IIndex> current = new ThreadLocal<IIndex>();
	private ThreadLocal<URI> currentProject = new ThreadLocal<URI>();
	private ThreadLocal<IndexPartitionDescriptor> currentPartition = new ThreadLocal<IndexPartitionDescriptor>();

	private IndexManager() {
		// use getInstance()
	}

	public static IndexManager getInstance() {
		return INSTANCE;
	}

	public IIndex getCurrent() {
		ensureInitialized();
		return current.get();
	}

	public URI getCurrentProject() {
		ensureInitialized();
		return currentProject.get();
	}

	public IndexPartitionDescriptor getCurrentPartition() {
		ensureInitialized();
		return currentPartition.get();
	}

	public boolean isInitialized() {
		return current.get() != null;
	}

	private void ensureInitialized() {
		if(!isInitialized())
			throw new IllegalStateException(
				"Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");
	}

	public static ReadWriteLock getTransactionLock() {
		return transactionLock;
	}

	public void setCurrentPartition(IndexPartitionDescriptor currentPartition) {
		this.currentPartition.set(currentPartition);
	}

	public long startTransaction(ITermFactory factory, IOAgent agent) {
		long rev = revisionProvider.getAndIncrement();
		IIndex currentIndex = current.get();
		currentIndex.getPartition(currentPartition.get()).setRevisionTime(rev, new Date());

		assert currentIndex instanceof Index; // Prevent multiple transactions.

		IIndex transactionIndex = new Index();
		transactionIndex.initialize(factory, agent);
		current.set(new TransactionIndex(currentIndex, transactionIndex, currentPartition.get()));

		return rev;
	}

	public void endTransaction() {
		TransactionIndex currentIndex = (TransactionIndex) current.get();
		IIndex index = currentIndex.getIndex();
		IIndex transactionIndex = currentIndex.getTransactionIndex();
		current.set(index);

		transactionLock.writeLock().lock();
		try {
			if(currentIndex.hasClearedCurrentPartition())
				index.clearPartition(currentIndex.getCurrentPartition());

			for(TemplateWithPartitionDescriptor entry : currentIndex.getRemovedEntries())
				index.remove(entry.getTemplate(), entry.getPartitionDescriptor());

			for(IStrategoAppl template : currentIndex.getRemovedAllEntries())
				index.removeAll(template);

			for(IndexEntry entry : transactionIndex.getAll())
				index.add(entry);

			transactionIndex.clearAll();
		} finally {
			transactionLock.writeLock().unlock();
		}
	}

	private static Object getSyncRoot() {
		return IndexManager.class;
	}

	public AtomicLong getRevisionProvider() {
		return revisionProvider;
	}

	public static boolean isKnownIndexingLanguage(String language) {
		synchronized(getSyncRoot()) {
			return indexedLanguages.contains(language);
		}
	}

	public IIndex createIndex(ITermFactory factory, IOAgent agent) {
		IIndex index = new Index();
		index.initialize(factory, agent);
		return index;
	}

	public IIndex getIndex(String absoluteProjectPath) {
		URI project = getProjectURIFromAbsolute(absoluteProjectPath);
		WeakReference<IIndex> indexRef = indexCache.get(project);
		IIndex index = indexRef == null ? null : indexRef.get();
		return index;
	}

	public IIndex loadIndex(String projectPath, String language, ITermFactory factory, IOAgent agent) {
		URI project = getProjectURI(projectPath, agent);
		synchronized(getSyncRoot()) {
			indexedLanguages.add(language);
			WeakReference<IIndex> indexRef = indexCache.get(project);
			IIndex index = indexRef == null ? null : indexRef.get();
			if(index == null) {
				File indexFile = getFile(project);
				if(indexFile.exists())
					index = tryReadFromFile(getFile(project), factory, agent);
			}
			if(index == null) {
				index = createIndex(factory, agent);
				NotificationCenter.notifyNewProject(project);
			}
			indexCache.put(project, new WeakReference<IIndex>(index));
			current.set(index);
			currentProject.set(project);
			return index;
		}
	}

	public void unloadIndex(String removedProjectPath, IOAgent agent) {
		URI removedProject = getProjectURI(removedProjectPath, agent);
		synchronized(getSyncRoot()) {
			WeakReference<IIndex> removedIndex = indexCache.remove(removedProject);

			IIndex index = current.get();
			if(index != null && index == removedIndex.get()) {
				current.set(null);
				currentPartition.set(null);
			}

			URI project = currentProject.get();
			if(project != null && project.equals(removedProject)) {
				currentProject.set(null);
			}
		}
	}

	public URI getProjectURI(String projectPath, IOAgent agent) {
		File file = new File(projectPath);
		if(!file.isAbsolute())
			file = new File(agent.getWorkingDir(), projectPath);
		return file.toURI();
	}

	public URI getProjectURIFromAbsolute(String projectPath) {
		File file = new File(projectPath);
		if(!file.isAbsolute())
			throw new RuntimeException("Project path is not absolute.");
		return file.toURI();
	}

	public IIndex tryReadFromFile(File file, ITermFactory factory, IOAgent agent) {
		try {
			IIndex index = createIndex(factory, agent);
			IStrategoTerm term = new TermReader(factory).parseFromFile(file.toString());
			return indexFactory.indexFromTerms(index, term, factory, true);
		} catch(Exception e) {
			if(!file.delete())
				throw new RuntimeException("Failed to load index from " + file.getName()
					+ ". The file could not be deleted, please manually delete the file and restart analysis.", e);
			else
				throw new RuntimeException("Failed to load index from " + file.getName()
					+ ". The file has been deleted, a new index will be created on the next analysis.", e);
		}
	}

	public void storeCurrent(ITermFactory factory) throws IOException {
		File file = getFile(currentProject.get());
		IStrategoTerm stored = indexFactory.toTerm(getCurrent(), factory, true);
		file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		try {
			SAFWriter.writeTermToSAFStream(stored, fos);
			fos.flush();
		} finally {
			fos.close();
		}
	}

	private File getFile(URI project) {
		File container = new File(new File(project), ".cache");
		container.mkdirs();
		return new File(container, "index.idx");
	}
}
