package org.spoofax.terms.attachments;

import static org.spoofax.terms.SimpleTermVisitor.tryGetListIterator;

import java.util.Iterator;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

/** 
 * An attachment for a parent pointer. Can be shared among multiple child nodes.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class ParentAttachment extends AbstractTermAttachment {
	
	private static final long serialVersionUID = -159325782499007370L;

	public static TermAttachmentType<ParentAttachment> TYPE =
		TermAttachmentType.create(ParentAttachment.class);

	private IStrategoTerm parent;
	
	private IStrategoTerm elementParent;

	/**
	 * Creates a new parent attachment.
	 * 
	 * Should not be called directly, as parent attachment instances
	 * should not be shared.
	 * 
	 * @param parent         The parent of this term
	 * @param elementParent  The direct 'Cons' node parent of a list element 
	 * 
	 * @see #setOrigin(ISimpleTerm, IStrategoTerm)
	 */
	private ParentAttachment(IStrategoTerm parent, IStrategoTerm elementParent) {
		this.parent = parent;
		this.elementParent = elementParent;
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
	public static void setParent(ISimpleTerm term, IStrategoTerm parent, IStrategoTerm elementParent) {
		// Not true: assert get(term) == null : "Term parent should only be assigned once";
		term.putAttachment(new ParentAttachment(parent, elementParent));
	}
	
	/**
	 * Copies a parent attachment from another term to the given term.
	 */
	public static void setParent(ISimpleTerm term, ParentAttachment parent) {
		if (parent != null)
			term.putAttachment(new ParentAttachment(parent.parent, parent.elementParent));
	}
	
	/**
	 * Gets the *original* parent of this term at the time of creation, if available.
	 */
	public static IStrategoTerm getParent(ISimpleTerm term) {
		if (term == null) return null;
		ParentAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getParent();
	}

	/**
	 * Gets the *original* root of this term tree at the time of creation, if available.
	 */
	public static IStrategoTerm getRoot(IStrategoTerm term) {
		IStrategoTerm parent = getParent(term); 
		while (parent != null) {
			term = parent;
			parent = getParent(term);
		}
		return term;
	}

	/**
	 * Gets the *original* root of this term tree at the time of creation, if available.
	 */
	public static ISimpleTerm getRoot(ISimpleTerm term) {
		ISimpleTerm parent = getParent(term); 
		while (parent != null) {
			term = parent;
			parent = ParentAttachment.getParent(term);
		}
		return term;
	}
	
	/**
	 * Gets the parent of an attachment, either through {@link #getParent(ISimpleTerm)}
	 * or through {@link #traverseGetParent()}.
	 */
	public static ISimpleTerm tryTraverseGetParent(ISimpleTerm child, ISimpleTerm root) {
		ISimpleTerm result = getParent(child);
		return result != null ? result : traverseGetParent(child, root);
	}

	/**
	 * Fetch the parent of a term by pure traversal from the root of the tree.
	 */
	public static ISimpleTerm traverseGetParent(ISimpleTerm child, ISimpleTerm root) {
		return traverseGetParent(child, root, null);
	}
	
	private static ISimpleTerm traverseGetParent(ISimpleTerm child, ISimpleTerm current, ISimpleTerm lastParent) {
		if (current == child) return lastParent;
		
		Iterator<ISimpleTerm> iterator = tryGetListIterator(current); 
		for (int i = 0, max = current.getSubtermCount(); i < max; i++) {
			ISimpleTerm currentChild = iterator == null ? current.getSubterm(i) : iterator.next();
			ISimpleTerm result = traverseGetParent(child, currentChild, current);
			if (result != null)
				return result;
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return "" + getParent();
	}
}
