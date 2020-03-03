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
		return () -> new StrategoListIterator(list);
	}

	@Override
	public boolean hasNext() {
		return !current.isEmpty();
	}

	@Override
	public IStrategoTerm next() {
		IStrategoTerm result = current.head();
		current = current.tail();
		return result;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
