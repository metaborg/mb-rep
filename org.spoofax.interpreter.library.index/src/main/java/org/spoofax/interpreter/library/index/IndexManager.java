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

	private final IndexFactory indexFactory = new IndexFactory();

	/** Map from project path to index. Access requires a lock on {@link #getSyncRoot} */
	private final Map<URI, WeakReference<IIndex>> indexes = new HashMap<URI, WeakReference<IIndex>>();

	/** Set of all languages that are being indexed. */
	private final Set<String> indexedLanguages = new HashSet<String>();

	/** The current index in the current thread. */
	private final ThreadLocal<IIndex> current = new ThreadLocal<IIndex>();

	/** The current project in the current thread. */
	private final ThreadLocal<URI> currentProject = new ThreadLocal<URI>();


	private IndexManager() {
		// use getInstance()
	}

	public static IndexManager getInstance() {
		return INSTANCE;
	}


	public boolean isInitialized() {
		return current.get() != null;
	}

	private void ensureInitialized() {
		if(!isInitialized())
			throw new IllegalStateException(
				"Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");
	}


	public IIndex getCurrent() {
		ensureInitialized();
		return current.get();
	}

	private void setCurrent(URI project, IIndex index) {
		current.set(index);
		indexes.put(project, new WeakReference<IIndex>(index));
	}

	private void setCurrent(IIndex taskEngine) {
		setCurrent(currentProject.get(), taskEngine);
	}

	public URI getCurrentProject() {
		ensureInitialized();
		return currentProject.get();
	}

	public boolean isKnownIndexingLanguage(String language) {
		synchronized(IndexManager.class) {
			return indexedLanguages.contains(language);
		}
	}


	private boolean isHierarchicalIndex(IIndex index) {
		return index instanceof IHierarchicalIndex;
	}

	public IIndex pushIndex(ITermFactory factory) {
		final IIndex currentIndex = current.get();
		final IIndex newIndex = createIndex(currentIndex, factory);
		setCurrent(newIndex);
		return newIndex;
	}

	public IIndex popIndex() {
		final IIndex currentIndex = current.get();
		if(!isHierarchicalIndex(currentIndex))
			throw new RuntimeException("Cannot pop the root index.");
		final IIndex parentIndex = ((IHierarchicalIndex) currentIndex).getParent();
		setCurrent(parentIndex);
		return parentIndex;
	}

	public IIndex popToRootIndex() {
		final IIndex currentIndex = current.get();
		if(!isHierarchicalIndex(currentIndex))
			return currentIndex;
		final IIndex parentIndex = ((IHierarchicalIndex) currentIndex).getParent();
		setCurrent(parentIndex);
		return popToRootIndex();
	}

	public IIndex mergeIndex() {
		final IIndex currentIndex = current.get();
		if(!isHierarchicalIndex(currentIndex))
			throw new RuntimeException("Cannot merge from the root index.");
		final IHierarchicalIndex currentHierarchicalIndex = (IHierarchicalIndex) currentIndex;
		final IIndex parentIndex = currentHierarchicalIndex.getParent();

		for(IndexPartition partition : currentHierarchicalIndex.getClearedPartitions())
			parentIndex.clearPartition(partition);

		for(IndexEntry entry : currentHierarchicalIndex.getAllCurrent())
			parentIndex.add(entry);

		setCurrent(parentIndex);
		return parentIndex;
	}


	public IIndex createIndex(ITermFactory factory) {
		return new Index(factory);
	}

	public IIndex createIndex(IIndex parent, ITermFactory factory) {
		return new HierarchicalIndex(new Index(factory), parent, factory);
	}


	public IIndex loadIndex(String projectPath, String language, ITermFactory factory, IOAgent agent) {
		URI project = getProjectURI(projectPath, agent);
		synchronized(IndexManager.class) {
			indexedLanguages.add(language);
			final WeakReference<IIndex> indexRef = indexes.get(project);
			IIndex index = indexRef == null ? null : indexRef.get();
			if(index == null) {
				File indexFile = getIndexFile(project);
				if(indexFile.exists())
					index = read(getIndexFile(project), factory, agent);
			}
			if(index == null) {
				index = createIndex(factory);
				for(String indexedLanguage : indexedLanguages)
					index.addLanguage(indexedLanguage);
				NotificationCenter.notifyNewProject(project);
			} else if(index.addLanguage(language)) {
				NotificationCenter.notifyNewProjectLanguage(project, language);
			}
			setCurrent(project, index);
			currentProject.set(project);
			return index;
		}
	}

	public void unloadIndex(String projectPath, IOAgent agent) {
		URI removedProject = getProjectURI(projectPath, agent);
		synchronized(IndexManager.class) {
			WeakReference<IIndex> removedIndex = indexes.remove(removedProject);

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


	public IIndex read(File file, ITermFactory factory, IOAgent agent) {
		try {
			IIndex index = createIndex(factory);
			IStrategoTerm term = new TermReader(factory).parseFromFile(file.toString());
			return indexFactory.indexFromTerm(index, agent, term, factory, true);
		} catch(Exception e) {
			if(!file.delete())
				throw new RuntimeException("Failed to load index from " + file.getName()
					+ ". The index file could not be deleted, please manually delete the file and restart analysis.", e);
			else
				throw new RuntimeException("Failed to load index from " + file.getName()
					+ ". The index file has been deleted, a new index will be created on the next analysis.", e);
		}
	}

	public void write(IIndex index, File file, ITermFactory factory) throws IOException {
		final IStrategoTerm serialized = indexFactory.indexToTerm(index, factory, true);
		file.createNewFile();
		final FileOutputStream fos = new FileOutputStream(file);
		try {
			SAFWriter.writeTermToSAFStream(serialized, fos);
			fos.flush();
		} finally {
			fos.close();
		}
	}

	public void writeCurrent(ITermFactory factory) throws IOException {
		write(getCurrent(), getIndexFile(getCurrentProject()), factory);
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


	public File getIndexFile(URI projectPath) {
		final File directory = new File(new File(projectPath), ".cache");
		directory.mkdirs();
		return new File(directory, "index.idx");
	}
}
