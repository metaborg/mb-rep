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
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;
import org.spoofax.terms.util.TermUtils;

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

	@Override
	public int getTermType() {
		return getWrapped().getTermType();
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
		return TermUtils.asList(getWrapped())
				.map(IStrategoList::head)
				.orElseThrow(() -> new TermWrapperException("Called head() on a term that is not of type LIST"));
	}

	@Override
	public IStrategoList tail() {
		return TermUtils.asList(getWrapped())
				.map(IStrategoList::tail)
				.orElseThrow(() -> new TermWrapperException("Called tail() on a term that is not of type LIST"));
	}

	@Override
	public boolean isEmpty() {
		return TermUtils.asList(getWrapped())
				.map(IStrategoList::isEmpty)
				.orElseThrow(() -> new TermWrapperException("Called isEmpty() on a term that is not of type LIST"));
	}

	@Deprecated
	@Override
	public IStrategoList prepend(IStrategoTerm prefix) {
		return TermUtils.asList(getWrapped())
				.map(l -> l.prepend(prefix))
				.orElseThrow(() -> new TermWrapperException("Called prepend() on a term that is not of type LIST"));
	}

	@Override
	public int size() {
		return TermUtils.asList(getWrapped())
				.map(IStrategoList::size)
				.orElse(TermUtils.asTuple(getWrapped())
						.map(IStrategoTuple::size)
						.orElseThrow(() -> new TermWrapperException("Called size() on a term that is not a LIST or TUPLE")));
	}

	@Override
	public IStrategoConstructor getConstructor() {
		return TermUtils.asAppl(getWrapped())
				.map(IStrategoAppl::getConstructor)
				.orElseThrow(() -> new TermWrapperException("Called getConstructor() on a term that is not of type APPL"));
	}

	@Override
	public String getName() {
		return TermUtils.asString(getWrapped())
				.map(IStrategoString::stringValue)
				.orElse(TermUtils.asAppl(getWrapped())
						.map(t -> t.getConstructor().getName())
						.orElseThrow(() -> new TermWrapperException("Called getName() on a term that is not of type STRING or APPL")));
	}

	@Override
	public int intValue() {
		return TermUtils.asInt(getWrapped())
				.map(IStrategoInt::intValue)
				.orElseThrow(() -> new TermWrapperException("Called intValue() on a term that is not of type INT"));
	}

	@Override
	@Deprecated
	public boolean isUniqueValueTerm() {
		return TermUtils.asInt(getWrapped())
				.map(IStrategoInt::isUniqueValueTerm)
				.orElseThrow(() -> new TermWrapperException("Called isUniqueValueTerm() on a term that is not of type INT"));
	}

	@Override
	public double realValue() {
		return TermUtils.asReal(getWrapped())
				.map(IStrategoReal::realValue)
				.orElseThrow(() -> new TermWrapperException("Called realValue() on a term that is not of type REAL"));
	}

	@Override
	public String stringValue() {
		return TermUtils.asString(getWrapped())
				.map(IStrategoString::stringValue)
				.orElseThrow(() -> new TermWrapperException("Called stringValue() on a term that is not of type STRING"));
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

}
