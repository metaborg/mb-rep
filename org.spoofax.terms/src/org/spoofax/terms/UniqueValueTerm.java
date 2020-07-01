package org.spoofax.terms;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.TermType;
import org.spoofax.terms.util.EmptyIterator;

/**
 * Used by the SRTS_EXT_newint_0_0 compatibility strategy.
 */
@Deprecated
public final class UniqueValueTerm extends AbstractSimpleTerm implements IStrategoInt {
	
	private static final long serialVersionUID = 2464633689395266636L;

	private static final AtomicInteger counter = new AtomicInteger();
	
	private final int value = counter.incrementAndGet();

	public int intValue() {
		return value;
	}

	@Override
	public List<IStrategoTerm> getSubterms() {
		return Collections.emptyList();
	}

	public IStrategoList getAnnotations() {
		return TermFactory.EMPTY_LIST;
	}

	public IStrategoTerm getSubterm(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public IStrategoTerm[] getAllSubterms() {
		return TermFactory.EMPTY_TERM_ARRAY;
	}

	public int getSubtermCount() {
		return 0;
	}

	@Deprecated
	public int getTermType() {
		return getType().getValue();
	}

	@Override
	public TermType getType() {
		return TermType.INT;
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
		return new EmptyIterator<>();
	}
	
}