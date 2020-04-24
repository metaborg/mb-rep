package org.spoofax.terms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoNamed;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermPrinter;

/**
 * A wrapped Stratego term of any type that supports attachments separate from its base term.
 * 
 * @see StrategoAnnotation  A term wrapped with additional annotations.
 * @see LazyTerm            A lazily initialized term.
 *
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class StrategoWrapped extends StrategoTerm implements IStrategoAppl, IStrategoInt, IStrategoList, IStrategoReal, IStrategoString, IStrategoTuple {
	
	
	private static final long serialVersionUID = 3470735405007721813L;
	
	private final IStrategoTerm wrapped;
	
	/**
	 * Creates a new wrapped Stratego term, copying its annotations.
	 */
	public StrategoWrapped(IStrategoTerm wrapped) {
		this(wrapped, wrapped.getAnnotations());
	}
	
	/**
	 * Creates a new wrapped Stratego term with custom annotations.
	 * Inheritor classes using this constructor should reimplement the
	 * following methods
	 * {@link #match(IStrategoTerm second)},
	 * {@link #hashFunction()},
	 * {@link #toString()}, and
	 * {@link #prettyPrint(ITermPrinter)}.
	 */
	protected StrategoWrapped(IStrategoTerm wrapped, IStrategoList annotations) {
		super(annotations);
		this.wrapped = wrapped;
	}
	
	public final IStrategoTerm getWrapped() {
		return wrapped;
	}
	
	// Common accessors

	@Override
	protected int hashFunction() {
		return wrapped.hashCode();
	}
	
	@Override
	protected boolean doSlowMatch(IStrategoTerm second) {
		return wrapped.match(second);
	}

	@Override
	public IStrategoTerm[] getAllSubterms() {
		return wrapped.getAllSubterms();
	}

	@Override
	public List<IStrategoTerm> getSubterms() {
		return wrapped.getSubterms();
	}

	@Override
	public IStrategoTerm getSubterm(int index) {
		return wrapped.getSubterm(index);
	}

	@Override
	public int getSubtermCount() {
		return wrapped.getSubtermCount();
	}

	@Override
	public int getTermType() {
		return wrapped.getTermType();
	}

	@Deprecated
	@Override
	public void prettyPrint(ITermPrinter pp) {
		wrapped.prettyPrint(pp);
	}

	@Override
	public String toString() {
		return wrapped.toString();
	}

	@Override
	public void writeAsString(Appendable output, int maxDepth) throws IOException {
		wrapped.writeAsString(output, maxDepth);
	}
	
	// Semi-specialized accessors

	@Override
	public final IStrategoTerm get(int index) {
		return getSubterm(index);
	}

	public final IStrategoTerm[] getArguments() {
		return wrapped.getAllSubterms();
	}
	
	// Specialized accessors

	@Override
	public IStrategoTerm head() {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called head() on a term that is not of type LIST");
		return ((IStrategoList) wrapped).head();
	}

	@Override
	public IStrategoList tail() {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called tail() on a term that is not of type LIST");
		return ((IStrategoList) wrapped).tail();
	}

	@Override
	public boolean isEmpty() {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called isEmpty() on a term that is not of type LIST");
		return ((IStrategoList) wrapped).isEmpty();
	}

	@Deprecated
	@Override
	public IStrategoList prepend(IStrategoTerm prefix) {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called prepend() on a term that is not of type LIST");
		return ((IStrategoList) wrapped).prepend(prefix);
	}

	@Override
	public int size() {
		switch (getTermType()) {
			case LIST:
				return ((IStrategoList) wrapped).size();
			case TUPLE:
				return ((IStrategoTuple) wrapped).size();
			default:
				throw new TermWrapperException("Called size() on a term that is not a LIST or TUPLE");
		}
	}

	@Override
	public IStrategoConstructor getConstructor() {
		if (getTermType() != APPL)
			throw new TermWrapperException("Called getConstructor() on a term that is not of type APPL");
		return ((IStrategoAppl) wrapped).getConstructor();
	}

	@Override
	public int intValue() {
		if (getTermType() != INT)
			throw new TermWrapperException("Called intValue() on a term that is not of type INT");
		return ((IStrategoInt) wrapped).intValue();
	}

	@Override
	@Deprecated
	public boolean isUniqueValueTerm() {
		if (getTermType() != INT)
			throw new TermWrapperException("Called isUniqueValueTerm() on a term that is not of type INT");
		return ((IStrategoInt) wrapped).isUniqueValueTerm();
	}

	@Override
	public double realValue() {
		if (getTermType() != REAL)
			throw new TermWrapperException("Called realValue() on a term that is not of type REAL");
		return ((IStrategoReal) wrapped).realValue();
	}

	@Override
	public String getName() {
		if (getTermType() != STRING && getTermType() != APPL)
			throw new TermWrapperException("Called getName() on a term that is not of type STRING or APPL");
		return ((IStrategoNamed) wrapped).getName();
	}

	@Override
	public String stringValue() {
		if (getTermType() != STRING)
			throw new TermWrapperException("Called stringValue() on a term that is not of type STRING");
		return ((IStrategoString) wrapped).stringValue();
	}

	public Iterator<IStrategoTerm> iterator() {
		return wrapped.iterator();
	}

}