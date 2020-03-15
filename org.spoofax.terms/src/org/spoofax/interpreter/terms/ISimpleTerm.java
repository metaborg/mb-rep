package org.spoofax.interpreter.terms;

import java.io.Serializable;

import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

import javax.annotation.Nullable;


/**
 * A simple tree-like interface for terms.
 */
public interface ISimpleTerm extends Serializable {

    /**
     * Gets the number of subterms of this term.
     *
     * @return the number of subterms of this term
     */
	int getSubtermCount();

    // FIXME: Should be named get() for Kotlin compatibility
    /**
     * Gets the subterm at the specified index.
     *
     * @param i the zero-based index of the subterm to find
     * @return the subterm at the specified index
     * @throws IndexOutOfBoundsException the index is out of bounds
     */
	ISimpleTerm getSubterm(int i);

	/**
	 * Gets the term attachment of the specified type,
     * if one is available for this term.
	 * 
	 * @param type the term attachment type, or {@code null} if the first attachment should be returned
     * @return the term attachment of the specified type; or {@code null} if it could not be found
	 */
    @SuppressWarnings("unchecked")
	default @Nullable <T extends ITermAttachment> T getAttachment(@Nullable TermAttachmentType<T> type) {
		ITermAttachment attachment = internalGetAttachment();
		if(type == null)
			return (T) attachment;
		for(ITermAttachment a = attachment; a != null; a = a.getNext()) {
			if(a.getAttachmentType() == type)
				return (T) a;
		}
		return null;
	}

    // FIXME: Return a new term when adding/changing an attachment. Make the term immutable.
    /**
     * Adds the specified term attachment to this term.
     * If a term attachment of this type is already present, it is replaced.
     *
     * @param attachmentToPut the attachment to add
     */
	default void putAttachment(ITermAttachment attachmentToPut) {
		ITermAttachment attachment = internalGetAttachment();
		if(attachmentToPut == null)
			return;
		if(attachment == null) {
			internalSetAttachment(attachmentToPut);
		} else {
			TermAttachmentType<?> newType = attachmentToPut.getAttachmentType();
			if(attachment.getAttachmentType() == newType) {
				attachmentToPut.setNext(attachment.getNext());
				internalSetAttachment(attachmentToPut);
			} else {
				ITermAttachment previous = attachment;
				for(ITermAttachment a = previous.getNext(); a != null; a = a.getNext()) {
					if(a.getAttachmentType() == newType) {
						attachmentToPut.setNext(a.getNext());
						previous.setNext(attachmentToPut);
						break;
					}
					previous = a;
				}
				previous.setNext(attachmentToPut);
			}
		}
	}

    // FIXME: Return a new term when removing an attachment. Make the term immutable.
    /**
     * Removes the term attachment of the specified type,
     * if one is available for this term.
     *
     * @param type the term attachment type
     * @return the removed term attachment; or {@code null} when no attachment was removed
     */
    default @Nullable ITermAttachment removeAttachment(TermAttachmentType<?> type) {
		ITermAttachment attachment = internalGetAttachment();
		if(attachment != null) {
			if(attachment.getAttachmentType() == type) {
				internalSetAttachment(attachment.getNext());
				attachment.setNext(null);
				return attachment;
			} else {
				ITermAttachment previous = attachment;
				for(ITermAttachment a = attachment.getNext(); a != null; a = a.getNext()) {
					if(a.getAttachmentType() == type) {
						previous.setNext(a.getNext());
						a.setNext(null);
						return a;
					}
					previous = a;
				}
			}
		}
		return null;
	}

    ITermAttachment internalGetAttachment();

    void internalSetAttachment(ITermAttachment attachment);

    /**
     * Gets whether this term is a list term.
     *
     * @return {@code true} when this term is a list term;
     * otherwise, {@code false}.
     * @deprecated Use {@link IStrategoTerm#getTermType()} instead.
     */
	@Deprecated
	boolean isList();
}