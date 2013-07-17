package org.spoofax.interpreter.library.index;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.index.notification.NotificationCenter;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.io.binary.SAFWriter;
import org.spoofax.terms.io.binary.TermReader;

public class IndexManager {
	private static final IndexManager INSTANCE = new IndexManager();
	private static final IndexFactory indexFactory = new IndexFactory();

	/**
	 * Indices by language and project. Access requires a lock on {@link #getSyncRoot}
	 */
	private static Map<URI, WeakReference<IIndex>> indexCache = new HashMap<URI, WeakReference<IIndex>>();
	private static Set<String> indexedLanguages = new HashSet<String>();

	private ThreadLocal<IIndex> current = new ThreadLocal<IIndex>();
	private ThreadLocal<URI> currentProject = new ThreadLocal<URI>();

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

	private void setCurrent(URI project, IIndex index) {
		current.set(index);
		indexCache.put(project, new WeakReference<IIndex>(index));
	}

	private void setCurrent(IIndex taskEngine) {
		setCurrent(currentProject.get(), taskEngine);
	}
	
	public URI getCurrentProject() {
		ensureInitialized();
		return currentProject.get();
	}

	public boolean isInitialized() {
		return current.get() != null;
	}

	private void ensureInitialized() {
		if(!isInitialized())
			throw new IllegalStateException(
				"Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");
	}

	public IIndex pushIndex(ITermFactory factory) {
		final IIndex currentIndex = current.get();
		final IIndex newIndex = createIndex(currentIndex, factory);
		setCurrent(newIndex);
		return newIndex;
	}

	public IIndex popIndex() {
		final IIndex currentIndex = current.get();
		final IIndex parentIndex = currentIndex.getParent();
		if(parentIndex == null || parentIndex instanceof EmptyIndex)
			throw new RuntimeException("Cannot pop the root index.");
		setCurrent(parentIndex);
		return parentIndex;
	}

	public IIndex mergeIndex() {
		final IIndex currentIndex = current.get();
		final IIndex parentIndex = currentIndex.getParent();
		if(parentIndex == null || parentIndex instanceof EmptyIndex)
			throw new RuntimeException("Cannot merge the root index.");

		for(IndexPartition partition : currentIndex.getClearedPartitions())
			parentIndex.clearPartition(partition);

		for(IndexEntry entry : currentIndex.getAllCurrent())
			parentIndex.add(entry);

		setCurrent(parentIndex);
		return parentIndex;
	}

	private static Object getSyncRoot() {
		return IndexManager.class;
	}

	public static boolean isKnownIndexingLanguage(String language) {
		synchronized(getSyncRoot()) {
			return indexedLanguages.contains(language);
		}
	}

	public IIndex createIndex(ITermFactory factory) {
		IIndex index = new Index(createEmptyIndex(), factory);
		return index;
	}

	public IIndex createIndex(IIndex parent, ITermFactory factory) {
		IIndex index = new Index(parent, factory);
		return index;
	}

	public IIndex createEmptyIndex() {
		return new EmptyIndex();
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
				index = createIndex(factory);
				NotificationCenter.notifyNewProject(project);
			}
			setCurrent(project, index);
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
			IIndex index = createIndex(factory);
			IStrategoTerm term = new TermReader(factory).parseFromFile(file.toString());
			return indexFactory.indexFromTerms(index, agent, term, factory, true);
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
