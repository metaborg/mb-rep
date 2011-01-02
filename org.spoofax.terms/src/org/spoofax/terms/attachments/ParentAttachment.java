package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.IStrategoTerm;

/** 
 * An attachment for a parent pointer. Can be shared among multiple child nodes.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class ParentAttachment extends AbstractTermAttachment {
	
	public static TermAttachmentType<ParentAttachment> TYPE =
		TermAttachmentType.create(ParentAttachment.class);

	private IStrategoTerm parent;
	
	private IStrategoTerm elementParent;

	public ParentAttachment() {
		// No default initialization
	}
	
	public TermAttachmentType<ParentAttachment> getAttachmentType() {
		return TYPE;
	}
	
	public static ParentAttachment get(IStrategoTerm term) {
		return term.getAttachment(TYPE);
	}
	
	/**
	 * Gets the *original* parent of this term at the time of creation, if available.
	 */
	public IStrategoTerm getParent() {
		if (parent == null && elementParent != null) {
			IStrategoTerm listAncestor = getParent(elementParent);
			parent = listAncestor == null ? elementParent : listAncestor;
		}
		return parent;
	}

	/**
	 * @param parent         The parent of this term
	 * @param elementParent  The direct 'Cons' node parent of a list element 
	 */
	public void setParent(IStrategoTerm parent, IStrategoTerm elementParent) {
		if (this.parent != null && this.parent != parent)
			throw new IllegalStateException("Term parent can only be assigned once");
		this.parent = parent;
		this.elementParent = elementParent;
	}
	
	/**
	 * Gets the *original* parent of this term at the time of creation, if available.
	 */
	public static IStrategoTerm getParent(IStrategoTerm term) {
		ParentAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getParent();
	}
	
	/**
	 * @param parent         The parent of this term, if available
	 * @param elementParent  The direct 'Cons' node parent of a list element 
	 */
	public static void setParent(IStrategoTerm term, IStrategoTerm parent, IStrategoTerm elementParent) {
		ParentAttachment attachment = term.getAttachment(TYPE);
		if (attachment == null || attachment.parent != null || attachment.elementParent != null) {
			attachment = new ParentAttachment();
			term.putAttachment(attachment);
		}
		attachment.parent = parent;
	}
	
}
