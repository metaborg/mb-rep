package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermVisitor;

/** 
 * @author Maartje de Jonge
 * use of this class: 
 * - DesugaredOriginAttachment is attached to the origin term of a term and stores the
 * desugared form of the origin term.  
 * - DesugaredOrigins are attached after the desugaring/analysis is applied 
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
	 * @see #setOrigin(ISimpleTerm, IStrategoTerm)
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
	
	public IStrategoTerm getDesugaredOrigin() {
		return desugaredOrigin;
	}

	public void setDesugaredOrigin(IStrategoTerm desugared) {
		this.desugaredOrigin = desugared;
	}
	
	public static IStrategoTerm getDesugaredOrigin(ISimpleTerm term) {
		IStrategoTerm origin = OriginAttachment.getOrigin(term);
		if (origin != null) term = origin;
		DesugaredOriginAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getDesugaredOrigin();
	}
		
	public static void setDesugaredOrigin(IStrategoTerm term, IStrategoTerm desugared) {
		OriginAttachment.tryGetOrigin(term).putAttachment(new DesugaredOriginAttachment(desugared));
	}
	
	@Override
	public String toString() {
		return "" + desugaredOrigin;
	}
	
	public static void setAllTermsAsDesugaredOrigins(IStrategoTerm trm) {
		class Visitor extends TermVisitor {
			public void preVisit(IStrategoTerm term) {
				DesugaredOriginAttachment.setDesugaredOrigin(term, term);
			}
		}
		Visitor visitor = new Visitor();
		visitor.visit(trm);
	}
}
