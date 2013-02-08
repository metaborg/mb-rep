package org.spoofax.interpreter.library.index;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.locks.Lock;

public class IndexEntryIterable extends AbstractIndexEntryIterable {
    private final Collection<IndexEntry> entries;

    public IndexEntryIterable(Collection<IndexEntry> entries, Lock lock) {
        super(lock);

        this.entries = entries;
    }

    public Iterator<IndexEntry> iterator() {
        return entries.iterator();
    }
}
