package org.spoofax.terms.attachments;

import static org.spoofax.terms.Term.isTermAppl;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermTransformer;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TermAttachmentSerializer {
	
	private final ITermFactory factory;

	public TermAttachmentSerializer(ITermFactory factory) {
		this.factory = factory;
	}
	
	public static void initialize(TermAttachmentType<?>... types) {
		// Once instantiated, the attachments initialize themselves
	}
	
	public IStrategoTerm toAnnotations(IStrategoTerm term) {
		final ITermFactory factory = this.factory;
		
		return new TermTransformer(factory, false) {
			@Override
			public IStrategoTerm preTransform(IStrategoTerm term) {
				IStrategoList results = null;
				ITermAttachment attachment = term.getAttachment(null);
				while (attachment != null) {
					if (attachment.getAttachmentType().isSerializationSupported()) {
						IStrategoTerm result = attachment.getAttachmentType().toTerm(factory, attachment);
						if (results == null) results = term.getAnnotations();
						results = factory.makeListCons(result, results);
					}
					attachment = attachment.getNext();
				}
				if (results != null)
					term = factory.annotateTerm(term, results);
				return term;
			}
		}.transform(term);
	}
	
	public IStrategoTerm fromAnnotations(IStrategoTerm term, boolean mutableOperations) {
		if (!mutableOperations)
			throw new NotImplementedException("fromAnnotations() with mutableOperations == false");
		
		final TermAttachmentType<?>[] types = TermAttachmentType.getKnownTypes();
		
		return new TermTransformer(factory, false) {
			@Override
			public IStrategoTerm preTransform(IStrategoTerm term) {
				IStrategoList annotations = term.getAnnotations();
				while (!annotations.isEmpty()) {
					IStrategoTerm head = annotations.head();
					if (isTermAppl(head)) {
						IStrategoAppl appl = (IStrategoAppl) head;
						IStrategoConstructor con = appl.getConstructor();
						for (TermAttachmentType<?> type : types)
							if (type.getTermConstructor() == con)
								term.putAttachment(type.fromTerm(appl)); // mutate
					}
				}
				return term;
			}
		}.transform(term);
	}
}
