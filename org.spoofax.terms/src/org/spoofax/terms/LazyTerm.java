package org.spoofax.terms;

import java.io.IOException;

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
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

/**
 * A lazily initialized term,
 * 
 * @see StrategoWrapped  A non-lazy wrapped term that supports attachments separate from its base term.
 *
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class LazyTerm implements IStrategoAppl, IStrategoInt, IStrategoList, IStrategoReal, IStrategoString, IStrategoTuple {
	
	private IStrategoTerm term;
	
	public final IStrategoTerm getWrapped() {
		if (term == null) term = init();
		return term;
	}
	
	public final IStrategoTerm getWrapped(boolean skipInit) {
		return skipInit ? term : getWrapped();
	}
	
	protected abstract IStrategoTerm init();
	
	// Common methods

	public IStrategoTerm[] getAllSubterms() {
		return getWrapped().getAllSubterms();
	}

	public IStrategoTerm getSubterm(int index) {
		return getWrapped().getSubterm(index);
	}

	public int getSubtermCount() {
		return getWrapped().getSubtermCount();
	}

	public int getTermType() {
		return getWrapped().getTermType();
	}

	@Deprecated
	public void prettyPrint(ITermPrinter pp) {
		getWrapped().prettyPrint(pp);
	}

	@Override
	public String toString() {
		return getWrapped().toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return getWrapped().equals(obj);
	}
	
	@Override
	public int hashCode() {
		return getWrapped().hashCode();
	}

	public IStrategoList getAnnotations() {
		return getWrapped().getAnnotations();
	}

	public int getStorageType() {
		return MUTABLE; // let's not spread these guys // Math.min(SHARABLE, getWrapped().getStorageType());
	}

	public boolean match(IStrategoTerm second) {
		return getWrapped().match(second);
	}
	
	// Semi-specialized accessors

	public final IStrategoTerm get(int index) {
		return getSubterm(index);
	}

	public final IStrategoTerm[] getArguments() {
		return getWrapped().getAllSubterms();
	}
	
	// Specialized accessors

	public IStrategoTerm head() {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called head() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).head();
	}

	public IStrategoList tail() {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called tail() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).tail();
	}

	public boolean isEmpty() {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called isEmpty() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).isEmpty();
	}

	@Deprecated
	public IStrategoList prepend(IStrategoTerm prefix) {
		if (getTermType() != LIST)
			throw new TermWrapperException("Called prepend() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).prepend(prefix);
	}

	public int size() {
		switch (getTermType()) {
			case LIST:
				return ((IStrategoList) getWrapped()).size();
			case TUPLE:
				return ((IStrategoTuple) getWrapped()).size();
			default:
				throw new TermWrapperException("Called size() on a term that is not a LIST or TUPLE");
		}
	}

	public IStrategoConstructor getConstructor() {
		if (getTermType() != APPL)
			throw new TermWrapperException("Called getConstructor() on a term that is not of type APPL");
		return ((IStrategoAppl) getWrapped()).getConstructor();
	}
	
	public String getName() {
		if (getTermType() != STRING && getTermType() != APPL)
			throw new TermWrapperException("Called getName() on a term that is not of type STRING or APPL");
		return ((IStrategoNamed) getWrapped()).getName();
	}

	public int intValue() {
		if (getTermType() != INT)
			throw new TermWrapperException("Called intValue() on a term that is not of type INT");
		return ((IStrategoInt) getWrapped()).intValue();
	}

	public boolean isUniqueValueTerm() {
		if (getTermType() != INT)
			throw new TermWrapperException("Called isUniqueValueTerm() on a term that is not of type INT");
		return ((IStrategoInt) getWrapped()).isUniqueValueTerm();
	}

	public double realValue() {
		if (getTermType() != REAL)
			throw new TermWrapperException("Called realValue() on a term that is not of type REAL");
		return ((IStrategoReal) getWrapped()).realValue();
	}

	public String stringValue() {
		if (getTermType() != STRING)
			throw new TermWrapperException("Called stringValue() on a term that is not of type STRING");
		return ((IStrategoString) getWrapped()).stringValue();
	}

	public String toString(int maxDepth) {
		return getWrapped().toString(maxDepth);
	}

	public void writeAsString(Appendable output, int maxDepth) throws IOException {
		getWrapped().writeAsString(output, maxDepth);
	}

	public<T extends ITermAttachment> T getAttachment(TermAttachmentType<T> attachmentType) {
		return getWrapped().getAttachment(attachmentType);
	}

    public void putAttachment(ITermAttachment attachment) {
    	getWrapped().putAttachment(attachment);
    }
    
    public ITermAttachment removeAttachment(TermAttachmentType<?> attachmentType) {
    	return getWrapped().removeAttachment(attachmentType);
    }
    
	public boolean isList() {
		return getWrapped().isList();
	}

}