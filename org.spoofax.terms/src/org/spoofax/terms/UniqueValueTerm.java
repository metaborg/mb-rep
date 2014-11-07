package org.spoofax.terms;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.EmptyIterator;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public final class UniqueValueTerm extends AbstractSimpleTerm implements IStrategoInt {
	
	private static final long serialVersionUID = 2464633689395266636L;

	private static final AtomicInteger counter = new AtomicInteger();
	
	private final int value = counter.incrementAndGet();

	public int intValue() {
		return value;
	}

	public IStrategoTerm[] getAllSubterms() {
		return TermFactory.EMPTY;
	}

	public IStrategoList getAnnotations() {
		return TermFactory.EMPTY_LIST;
	}

	public int getStorageType() {
		return MUTABLE; // allow attachments
	}

	public IStrategoTerm getSubterm(int index) {
		throw new UnsupportedOperationException();
	}

	public int getSubtermCount() {
		return 0;
	}

	public int getTermType() {
		return INT;
	}
	
	public boolean isUniqueValueTerm() {
		return true;
	}

	public void prettyPrint(ITermPrinter pp) {
		pp.print(String.valueOf(value));
	}

	public boolean match(IStrategoTerm second) {
		return second == this;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}
	
	@Override
	public int hashCode() {
		// Always different from basic stratego int hash
		return (449 * value ^ 7841) + 1;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	public String toString(int maxDepth) {
		return toString();
	}

	public void writeAsString(Appendable output, int maxDepth)
			throws IOException {
		output.append(toString());
	}

	public boolean isList() {
		return false;
	}

	public Iterator<IStrategoTerm> iterator() {
		return new EmptyIterator<IStrategoTerm>();
	}
	
}