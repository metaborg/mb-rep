package org.spoofax.interpreter.library.index.util;

import java.util.Iterator;

import com.google.common.collect.Iterators;

public class EmptyIterable<T> implements Iterable<T> {
	public Iterator<T> iterator() {
		return Iterators.emptyIterator();
	}
}
