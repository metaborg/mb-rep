package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.TermConverter;
import org.spoofax.terms.AbstractTermFactory;
import org.spoofax.terms.StrategoListIterator;

/**
 * Strips all attachments from a term
 * 
 * @see jsglr.shared.ImploderAttachment#getCompressedPositionAttachment()
 *      Creates a compact position information attachment for a term.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TermAttachmentStripper {
	
	private final ITermFactory factory;
	
	private final TermConverter converter;

	public TermAttachmentStripper(ITermFactory factory) {
		this.factory = factory;
		this.converter = new TermConverter(factory);
	}
	
	public IStrategoTerm strip(IStrategoTerm term) {
		if (term == null)
			return null;
		
		// Kids
		boolean isRebuildNeeded = false;
		IStrategoTerm[] kids = AbstractTermFactory.EMPTY_TERM_ARRAY;
		if (term.getSubtermCount() > 0) {
			kids = term.getAllSubterms();
			IStrategoTerm[] newKids = tryStripKids(kids);
			if (newKids != null) {
				isRebuildNeeded = true;
				kids = newKids;
			}
		}
		
		// Annotations
		IStrategoList annos = term.getAnnotations();
		IStrategoTerm[] newAnnos = tryStripAnnos(annos);
		if (newAnnos != null) {
			isRebuildNeeded = true;
			annos = factory.makeList(newAnnos);
		}
		
		// Self
		if (!isRebuildNeeded)
			isRebuildNeeded = term.getAttachment(null) != null;
		
		return isRebuildNeeded
			? converter.convertShallow(term, kids, annos)
			: term;
	}
	
	private IStrategoTerm[] tryStripKids(IStrategoTerm[] oldKids) {
		boolean isChanged = false;
		IStrategoTerm[] newKids = null;
		for (int i = 0; i < oldKids.length; i++) {
			IStrategoTerm oldKid = oldKids[i];
			IStrategoTerm newKid = strip(oldKid);
			if (!isChanged && oldKid != newKid) {
				newKids = oldKids.clone();
				isChanged = true;
			}
			if (isChanged)
				newKids[i] = newKid;
		}
		return newKids;
	}
	
	private IStrategoTerm[] tryStripAnnos(IStrategoList oldAnnos) {
		if (oldAnnos.isEmpty()) return null;
		boolean isChanged = false;
		IStrategoTerm[] newAnnos = new IStrategoTerm[oldAnnos.size()];
		StrategoListIterator oldIterator = new StrategoListIterator(oldAnnos);
		for (int i = 0; i < newAnnos.length; i++) {
			IStrategoTerm oldAnno = oldIterator.next();
			IStrategoTerm newAnno = strip(oldAnno);
			isChanged |= oldAnno != newAnno;
			newAnnos[i] = newAnno;
		}
		return isChanged ? newAnnos : null;
	}
}
