package org.spoofax.terms.attachments;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoTerm;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class OriginAttachment extends AbstractTermAttachment {
	
	private static final long serialVersionUID = 1180953352629370705L;

	public static TermAttachmentType<OriginAttachment> TYPE =
		new VolatileTermAttachmentType<OriginAttachment>(OriginAttachment.class);
	
	private IStrategoTerm origin;

	/**
	 * Creates a new origin attachment.
	 * 
	 * Should not be called directly, as origin attachment instances
	 * should not be shared.
	 * 
	 * @see #setOrigin(ISimpleTerm, IStrategoTerm)
	 */
	private OriginAttachment(IStrategoTerm origin) {
		this.origin = origin;
	}
	
	public TermAttachmentType<OriginAttachment> getAttachmentType() {
		return TYPE;
	}
	
	public static OriginAttachment get(ISimpleTerm term) {
		return term.getAttachment(TYPE);
	}
	
	public IStrategoTerm getOrigin() {
		return origin;
	}

	public void setOrigin(IStrategoTerm origin) {
		this.origin = origin;
	}
	
	public static IStrategoTerm getOrigin(ISimpleTerm term) {
		OriginAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? null : attachment.getOrigin();
	}
	
	public static IStrategoTerm tryGetOrigin(IStrategoTerm term) {
		OriginAttachment attachment = term.getAttachment(TYPE);
		return attachment == null ? term : attachment.getOrigin();
	}
	
	public static void setOrigin(ISimpleTerm term, IStrategoTerm origin) {
		/* Let's not assume there's a reusable origin attachment
		OriginAttachment attachment = term.getAttachment(TYPE);
		if (attachment == null) {
			attachment = new OriginAttachment();
			term.putAttachment(attachment);
		}
		attachment.origin = origin;
		*/
		term.putAttachment(new OriginAttachment(origin));
	}
	
	
	@Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        return result;
    }

    @Override public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        OriginAttachment other = (OriginAttachment) obj;
        if(origin == null) {
            if(other.origin != null)
                return false;
        } else if(!origin.equals(other.origin))
            return false;
        return true;
    }

    @Override
	public String toString() {
		return "" + origin;
	}
	
}
