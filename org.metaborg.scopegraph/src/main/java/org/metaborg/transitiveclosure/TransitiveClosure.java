package org.metaborg.transitiveclosure;

import org.pcollections.PSet;

import pcollections.HashTreePMultimap;
import pcollections.PMultimap;

public class TransitiveClosure<T> {

    private final PMultimap<T,T> forward;
    private final PMultimap<T,T> backward;

    public TransitiveClosure() {
        this.forward = new HashTreePMultimap<>();
        this.backward = new HashTreePMultimap<>();
    }

    private TransitiveClosure(PMultimap<T,T> forward, PMultimap<T,T> backward) {
        this.forward = forward;
        this.backward = backward;
    }

    public TransitiveClosure<T> add(T first, T second) throws SymmetryException {
        PSet<T> newValues = forward.get(second).plus(second);
        if (newValues.contains(first)) {
            throw new SymmetryException();
        }
        PMultimap<T,T> newForward = forward.plusAll(first, newValues);
        PMultimap<T,T> newBackward = backward;
        for (T t : backward.get(first)) {
            newBackward = newBackward.plusAll(t, newValues);
        }
        newBackward.plus(second, first);
        return new TransitiveClosure<>(newForward, newBackward);
    }

}