package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * A term attachment type that cannot be written from or to terms.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class VolatileTermAttachmentType<T extends ITermAttachment> extends TermAttachmentType<T> {

	public VolatileTermAttachmentType(Class<T> type) {
		super(type, null, 0);
	}

	@Override
	protected IStrategoTerm[] toSubterms(ITermFactory factory, T attachment) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected T fromSubterms(IStrategoTerm[] subterms) {
		throw new UnsupportedOperationException();
	}

}
