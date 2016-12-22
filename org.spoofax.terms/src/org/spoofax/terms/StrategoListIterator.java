package org.spoofax.terms;

import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class StrategoListIterator implements Iterator<IStrategoTerm> {
	
	private IStrategoList current;

	public StrategoListIterator(IStrategoList list) {
		current = list;
	}
	
	public static Iterable<IStrategoTerm> iterable(final IStrategoList list) {
		if (list instanceof Iterable)
			return ((Iterable<IStrategoTerm>) list);
		return new Iterable<IStrategoTerm>() {
			public Iterator<IStrategoTerm> iterator() {
				return new StrategoListIterator(list);
			}
		};
	}

	public boolean hasNext() {
		return !current.isEmpty();
	}

	public IStrategoTerm next() {
		IStrategoTerm result = current.head();
		current = current.tail();
		return result;
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
