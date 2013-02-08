package org.spoofax.interpreter.library.index;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

public abstract class AbstractIndexEntryIterable implements IIndexEntryIterable {
    private final Lock lock;

    public AbstractIndexEntryIterable(Lock lock) {
        this.lock = lock;
    }

    public abstract Iterator<IndexEntry> iterator();
    
    public IndexEntry[] toArray() {
        ArrayList<IndexEntry> entries = new ArrayList<IndexEntry>();
        for(IndexEntry entry : this)
            entries.add(entry);
        return entries.toArray(new IndexEntry[0]);
    }

    public void lock() {
        if(lock != null)
            lock.lock();
    }

    public void unlock() {
        if(lock != null)
            lock.unlock();
    }
}
