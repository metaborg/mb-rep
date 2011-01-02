package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.IStrategoTerm;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class OriginAttachment extends AbstractTermAttachment {
	
	public static TermAttachmentType<OriginAttachment> TYPE =
		TermAttachmentType.create(OriginAttachment.class);

	private IStrategoTerm parent;
	
	private IStrategoTerm origin;

	public OriginAttachment() {
		// No default initialization
	}
	
	public TermAttachmentType<OriginAttachment> getAttachmentType() {
		return TYPE;
	}
	
	public static OriginAttachment get(IStrategoTerm term) {
		return term.getAttachment(TYPE);
	}
	
	/**
	 * Gets the *original* parent of this term at the time of creation, if available.
	 */
	public IStrategoTerm getParent() {
		return parent;
	}

	public void setParent(IStrategoTerm parent) {
		this.parent = parent;
	}
	
	public IStrategoTerm getOrigin() {
		return origin;
	}

	public void setOrigin(IStrategoTerm origin) {
		this.origin = origin;
	}
	
	/**
	 * Gets the *original* parent of this term at the time of creation, if available.
	 */
	public static IStrategoTerm getParent(IStrategoTerm term) {
		OriginAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getParent();
	}
	
	public static IStrategoTerm getOrigin(IStrategoTerm term) {
		OriginAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getParent();
	}
	
	public static void setParent(IStrategoTerm term, IStrategoTerm parent) {
		OriginAttachment attachment = term.getAttachment(TYPE);
		if (attachment == null) {
			attachment = new OriginAttachment();
			term.putAttachment(attachment);
		}
		attachment.parent = parent;
	}
	
	public static void setOrigin(IStrategoTerm term, IStrategoTerm origin) {
		OriginAttachment attachment = term.getAttachment(TYPE);
		if (attachment == null) {
			attachment = new OriginAttachment();
			term.putAttachment(attachment);
		}
		attachment.origin = origin;
	}
	
}
