package org.spoofax.interpreter.library.index;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
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
    private final static IndexFactory indexFactory = new IndexFactory();

    /**
     * Indices by language and project. Access requires a lock on {@link #getSyncRoot}
     */
    private static Map<URI, WeakReference<IIndex>> indexCache = new HashMap<URI, WeakReference<IIndex>>();
    private static Set<String> indexedLanguages = new HashSet<String>();

    private ThreadLocal<IIndex> current = new ThreadLocal<IIndex>();
    private ThreadLocal<URI> currentProject = new ThreadLocal<URI>();
    private ThreadLocal<IndexPartitionDescriptor> currentPartition = new ThreadLocal<IndexPartitionDescriptor>();

    public static ReadWriteLock getTransactionLock() {
        return transactionLock;
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

    public boolean isInitialized() {
        return current.get() != null;
    }

    private void ensureInitialized() {
        if(!isInitialized())
            throw new IllegalStateException(
                "Index has not been set-up, use index-setup(|language, project-paths) to set up the index before use.");
    }

    public static boolean isKnownIndexingLanguage(String language) {
        synchronized(getSyncRoot()) {
            return indexedLanguages.contains(language);
        }
    }

    public void loadIndex(URI project, String language, ITermFactory factory, IOAgent agent) {
        synchronized(getSyncRoot()) {
        	indexedLanguages.add(language);
            WeakReference<IIndex> indexRef = indexCache.get(project);
            IIndex index = indexRef == null ? null : indexRef.get();
            if(index == null) {
                index = tryReadFromFile(getIndexFile(project), factory, agent);
            }
            if(index == null) {
                index = new Index();
                NotificationCenter.notifyNewProject(project);
            }
            indexCache.put(project, new WeakReference<IIndex>(index));
            current.set(index);
            currentProject.set(project);
        }
    }

    public IIndex tryReadFromFile(File file, ITermFactory factory, IOAgent agent) {
        IIndex index = new Index(); // TODO: Don't create concrete implementation here.
        index.initialize(factory, agent);
        try {
            IStrategoTerm term = new TermReader(factory).parseFromFile(file.toString());
            return indexFactory.indexFromTerms(index, term, factory, true);
        } catch(Exception e) {
            return null;
        }
    }

    public void storeCurrent(ITermFactory factory) throws IOException {
        File file = getIndexFile(currentProject.get());
        IStrategoTerm stored = indexFactory.toTerm(getCurrent(), factory, true);
        Writer writer = new BufferedWriter(new FileWriter(file));
        try {
            stored.writeAsString(writer, IStrategoTerm.INFINITE);
        } finally {
            writer.close();
        }
    }

    private File getIndexFile(URI project) {
        File container = new File(new File(project), ".cache");
        container.mkdirs();
        return new File(container, "index.idx");
    }
}
