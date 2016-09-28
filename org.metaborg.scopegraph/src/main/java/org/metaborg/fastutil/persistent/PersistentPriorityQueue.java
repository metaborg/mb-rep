package org.metaborg.fastutil.persistent;

import org.apache.commons.lang3.tuple.Pair;

public interface PersistentPriorityQueue<T> {

    PersistentPriorityQueue<T> enqueue(T elem);

    Pair<T,? extends PersistentPriorityQueue<T>> dequeue();

    boolean isEmpty();

    int size();

}
