package org.spoofax.terms;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

/**
 * Wraps any term with annotations.
 * 
 * Uses the attachments of the wrapped term, rather than
 * its own set of attachments.
 * 
 * @see ITermFactory#annotateTerm(IStrategoTerm, IStrategoList)
 *          Should generally be used to efficiently annotate a term.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class StrategoAnnotation extends StrategoWrapped {
	
	private final int storageType;
	
	private final ITermFactory factory;
	
	protected StrategoAnnotation(ITermFactory factory, IStrategoTerm term, IStrategoList annotations, int storageType) {
		super(term, annotations);
		
		if (!term.getAnnotations().isEmpty())
			throw new IllegalArgumentException("Annotated term cannot already have annotations");
		
		this.factory = factory;
		this.storageType = storageType;
		
		if (storageType != MUTABLE)
			initImmutableHashCode();
	}
	
	@Override
	protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
		IStrategoTerm wrapped = getWrapped();
		IStrategoList annotations = getAnnotations();
		IStrategoList secondAnnotations = second.getAnnotations();
		
		if (annotations == secondAnnotations) {
        	// Do nothing
        } else if (annotations.match(secondAnnotations)) {
        	if (commonStorageType == SHARABLE) internalSetAnnotations(secondAnnotations);
        } else {
        	return false;
        }
		
		if (annotations.isEmpty()) {
			return wrapped.match(second);
		} else {
			second = factory.annotateTerm(second, TermFactory.EMPTY_LIST);
			return wrapped.match(second);
		}
	}

	@Override
	protected int hashFunction() {
		assert getWrapped().getAnnotations().isEmpty() : "Constructor contract broken";
		return getWrapped().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		try {
			getWrapped().writeToString(result, Integer.MAX_VALUE);
			appendAnnotations(result, Integer.MAX_VALUE);
		} catch (IOException e) {
			throw new RuntimeException(e); // shan't happen
		}
		return result.toString();
	}
	
	public void writeToString(Appendable output, int maxDepth) throws IOException {
		getWrapped().writeToString(output, maxDepth);
		appendAnnotations(output, maxDepth);
	}
	
	@Override
	@Deprecated
	public void prettyPrint(ITermPrinter pp) {
		getWrapped().prettyPrint(pp);
		printAnnotations(pp);
	}

	@Override
	public int getStorageType() {
		return storageType;
	}

	@Override
	@Deprecated
	public IStrategoList prepend(IStrategoTerm prefix) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public <T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type) {
		return getWrapped().getAttachment(type);
	}
	
	@Override
	public void putAttachment(ITermAttachment attachment) {
		getWrapped().putAttachment(attachment);
	}
	
	@Override
	public void removeAttachment(TermAttachmentType<?> attachmentType) {
		getWrapped().removeAttachment(attachmentType);
	}
	
}
