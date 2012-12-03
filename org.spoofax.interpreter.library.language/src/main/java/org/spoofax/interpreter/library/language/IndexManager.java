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
 * @author Gabriël Konat
 */
public class IndexManager {
    private final static AtomicLong revisionProvider = new AtomicLong();
    private final static ReadWriteLock transactionLock = new ReentrantReadWriteLock();

    /**
     * Indices by language and project. Access requires a lock on {@link #getSyncRoot}
     */
    private static Map<String, Map<URI, WeakReference<IIndex>>> asyncIndexCache =
        new HashMap<String, Map<URI, WeakReference<IIndex>>>();

    private ThreadLocal<IIndex> current = new ThreadLocal<IIndex>();
    private ThreadLocal<URI> currentProject = new ThreadLocal<URI>();
    private ThreadLocal<String> currentLanguage = new ThreadLocal<String>();
    private ThreadLocal<IndexPartitionDescriptor> currentPartition = new ThreadLocal<IndexPartitionDescriptor>();

    public static ReadWriteLock getTransactionLock() {
        return transactionLock;
    }

    public IIndex getCurrent() {
        if(!isInitialized())
            throw new IllegalStateException(
                "Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");

        return current.get();
    }

    public IndexPartitionDescriptor getCurrentPartition() {
        if(!isInitialized())
            throw new IllegalStateException(
                "Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");

        return currentPartition.get();
    }

    public void setCurrentPartition(IndexPartitionDescriptor currentPartition) {
        this.currentPartition.set(currentPartition);
    }

    public URI getCurrentProject() {
        if(!isInitialized())
            throw new IllegalStateException(
                "Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");

        return currentProject.get();
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
                index.removePartition(currentIndex.getCurrentPartition());

            for(TemplateWithPartitionDescriptor entry : currentIndex.getRemovedEntries())
                index.remove(entry.getTemplate(), entry.getPartitionDescriptor());

            for(IndexEntry entry : transactionIndex.getAllEntries())
                index.add(entry);
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

    public boolean isInitialized() {
        return current.get() != null;
    }

    public static boolean isKnownIndexingLanguage(String language) {
        synchronized(getSyncRoot()) {
            return asyncIndexCache.containsKey(language);
        }
    }

    public void loadIndex(String language, URI project, ITermFactory factory, IOAgent agent) {
        synchronized(getSyncRoot()) {
            Map<URI, WeakReference<IIndex>> indicesByProject = asyncIndexCache.get(language);
            if(indicesByProject == null) {
                indicesByProject = new HashMap<URI, WeakReference<IIndex>>();
                asyncIndexCache.put(language, indicesByProject);
            }
            WeakReference<IIndex> indexRef = indicesByProject.get(project);
            IIndex index = indexRef == null ? null : indexRef.get();
            if(index == null) {
                index = tryReadFromFile(getIndexFile(project, language), factory, agent);
            }
            if(index == null) {
                index = new Index();
                NotificationCenter.notifyNewProject(project);
            }
            indicesByProject.put(project, new WeakReference<IIndex>(index));
            current.set(index);
            currentLanguage.set(language);
            currentProject.set(project);
        }
    }

    public IIndex tryReadFromFile(File file, ITermFactory factory, IOAgent agent) {
        try {
            IStrategoTerm term = new TermReader(factory).parseFromFile(file.toString());
            return Index.fromTerm(term, factory, agent, true); // TODO: Move to other class
        } catch(IOException e) {
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
