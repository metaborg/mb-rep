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
import org.spoofax.interpreter.terms.TermType;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

/**
 * A lazily initialized term,
 * 
 * @see StrategoWrapped A non-lazy wrapped term that supports attachments separate from its base term.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class LazyTerm implements IStrategoAppl, IStrategoInt, IStrategoList, IStrategoReal, IStrategoString,
	IStrategoTuple {

	private static final long serialVersionUID = 4886871162797987326L;

	private IStrategoTerm term;

	public final IStrategoTerm getWrapped() {
		if(term == null)
			term = init();
		return term;
	}

	public final IStrategoTerm getWrapped(boolean skipInit) {
		return skipInit ? term : getWrapped();
	}

	protected abstract IStrategoTerm init();

	// Common methods

	@Override
	public IStrategoTerm[] getAllSubterms() {
		return getWrapped().getAllSubterms();
	}

	@Override
	public List<IStrategoTerm> getSubterms() {
		return getWrapped().getSubterms();
	}

	@Override
	public IStrategoTerm getSubterm(int index) {
		return getWrapped().getSubterm(index);
	}

	@Override
	public int getSubtermCount() {
		return getWrapped().getSubtermCount();
	}

	@Deprecated
	@Override
	public int getTermType() {
		return getType().getValue();
	}

	@Override
	public TermType getType() {
		return getWrapped().getType();
	}

	@Deprecated
	@Override
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

	@Override
	public IStrategoList getAnnotations() {
		return getWrapped().getAnnotations();
	}

	@Override
	public boolean match(IStrategoTerm second) {
		return getWrapped().match(second);
	}

	// Semi-specialized accessors

	@Override
	public final IStrategoTerm get(int index) {
		return getSubterm(index);
	}

	public final IStrategoTerm[] getArguments() {
		return getWrapped().getAllSubterms();
	}

	// Specialized accessors

	@Override
	public IStrategoTerm head() {
		if(getType() != TermType.LIST)
			throw new TermWrapperException("Called head() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).head();
	}

	@Override
	public IStrategoList tail() {
		if(getType() != TermType.LIST)
			throw new TermWrapperException("Called tail() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).tail();
	}

	@Override
	public boolean isEmpty() {
		if(getType() != TermType.LIST)
			throw new TermWrapperException("Called isEmpty() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).isEmpty();
	}

	@Deprecated
	@Override
	public IStrategoList prepend(IStrategoTerm prefix) {
		if(getType() != TermType.LIST)
			throw new TermWrapperException("Called prepend() on a term that is not of type LIST");
		return ((IStrategoList) getWrapped()).prepend(prefix);
	}

	@Override
	public int size() {
		switch(getType()) {
			case LIST:
				return ((IStrategoList) getWrapped()).size();
			case TUPLE:
				return ((IStrategoTuple) getWrapped()).size();
			default:
				throw new TermWrapperException("Called size() on a term that is not a LIST or TUPLE");
		}
	}

	@Override
	public IStrategoConstructor getConstructor() {
		if(getType() != TermType.APPL)
			throw new TermWrapperException("Called getConstructor() on a term that is not of type APPL");
		return ((IStrategoAppl) getWrapped()).getConstructor();
	}

	@Override
	public String getName() {
		if(getType() != TermType.STRING && getType() != TermType.APPL)
			throw new TermWrapperException("Called getName() on a term that is not of type STRING or APPL");
		return ((IStrategoNamed) getWrapped()).getName();
	}

	@Override
	public int intValue() {
		if(getType() != TermType.INT)
			throw new TermWrapperException("Called intValue() on a term that is not of type INT");
		return ((IStrategoInt) getWrapped()).intValue();
	}

	@Override
	@Deprecated
	public boolean isUniqueValueTerm() {
		if(getType() != TermType.INT)
			throw new TermWrapperException("Called isUniqueValueTerm() on a term that is not of type INT");
		return ((IStrategoInt) getWrapped()).isUniqueValueTerm();
	}

	@Override
	public double realValue() {
		if(getType() != TermType.REAL)
			throw new TermWrapperException("Called realValue() on a term that is not of type REAL");
		return ((IStrategoReal) getWrapped()).realValue();
	}

	@Override
	public String stringValue() {
		if(getType() != TermType.STRING)
			throw new TermWrapperException("Called stringValue() on a term that is not of type STRING");
		return ((IStrategoString) getWrapped()).stringValue();
	}

	@Override
	public String toString(int maxDepth) {
		return getWrapped().toString(maxDepth);
	}

	@Override
	public void writeAsString(Appendable output, int maxDepth) throws IOException {
		getWrapped().writeAsString(output, maxDepth);
	}

	@Override
	public <T extends ITermAttachment> T getAttachment(TermAttachmentType<T> attachmentType) {
		return getWrapped().getAttachment(attachmentType);
	}

	@Override
	public void putAttachment(ITermAttachment attachment) {
		getWrapped().putAttachment(attachment);
	}

	@Override
	public ITermAttachment removeAttachment(TermAttachmentType<?> attachmentType) {
		return getWrapped().removeAttachment(attachmentType);
	}

	@Override
	@Deprecated
	public boolean isList() {
		return getWrapped().isList();
	}

	public Iterator<IStrategoTerm> iterator() {
		return getWrapped().iterator();
	}

}
