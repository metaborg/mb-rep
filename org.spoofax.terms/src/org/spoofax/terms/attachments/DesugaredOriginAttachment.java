package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;

/** 
 * @author Maartje de Jonge
 * - DesugaredOriginAttachment is attached during analysis/desugaring and stores the
 * desugared form of the origin term.  
 */
public class DesugaredOriginAttachment extends AbstractTermAttachment {

	private static final long serialVersionUID = 314969147583453260L;
	
	public static TermAttachmentType<DesugaredOriginAttachment> TYPE =
		new VolatileTermAttachmentType<DesugaredOriginAttachment>(DesugaredOriginAttachment.class);
	
	protected IStrategoTerm desugaredOrigin;

	/**
	 * Creates a new desugared origin attachment.
	 * 
	 * Should not be called directly, as origin attachment instances
	 * should not be shared.
	 * 
	 * @see #setDesugaredOrigin(ISimpleTerm, IStrategoTerm)
	 */
	protected DesugaredOriginAttachment(IStrategoTerm desugared) {
		this.desugaredOrigin = desugared;
	}
	
	public TermAttachmentType<DesugaredOriginAttachment> getAttachmentType() {
		return TYPE;
	}
	
	public static DesugaredOriginAttachment get(ISimpleTerm term) {
		return term.getAttachment(TYPE);
	}
	
	private IStrategoTerm getDesugaredOrigin() {
		return desugaredOrigin;
	}

	public static IStrategoTerm getDesugaredOrigin(IStrategoTerm term) {
		DesugaredOriginAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getDesugaredOrigin();
	}
		
	public static void setDesugaredOrigin(IStrategoTerm term, IStrategoTerm desugared) {
		/*
		assert(
			DesugaredOriginAttachment.getDesugaredOrigin(term) == null ||
			DesugaredOriginAttachment.getDesugaredOrigin(term) == desugared
		) : "Desugared origin is set only once";
		assert(OriginAttachment.getOrigin(desugared) != null) : 
			"desugared origin term must have an origin term";
		if(DesugaredOriginAttachment.getDesugaredOrigin(term) == null)
		*/
		term.putAttachment(new DesugaredOriginAttachment(desugared));
	}
	
	@Override
	public String toString() {
		return "" + desugaredOrigin;
	}
/*	
	public static void setAllTermsAsDesugaredOrigins(IStrategoTerm trm) {
		class Visitor extends TermVisitor {
			public void preVisit(IStrategoTerm visited) {
				if(OriginAttachment.getOrigin(visited) != null)
					DesugaredOriginAttachment.setDesugaredOrigin(visited, visited);
			}
		}
		Visitor visitor = new Visitor();
		visitor.visit(trm);
		System.out.println();
	}
*/
}
