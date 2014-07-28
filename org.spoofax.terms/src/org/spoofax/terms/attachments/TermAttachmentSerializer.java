package org.spoofax.terms.attachments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.TermTransformer;
import static org.spoofax.terms.Term.*;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TermAttachmentSerializer {
	
	final ITermFactory factory;

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
						
						//IStrategoTerm result = tryConvertAttachmentTypeToTerm(factory, attachment);
						
						IStrategoTerm result =  attachment.getAttachmentType().toTerm(factory, attachment);
						if (results == null) results = term.getAnnotations();
						results = (result ==null) ? results : factory.makeListCons(result, results);
					}
					attachment = attachment.getNext();
				}
				if (results != null)
					term = factory.annotateTerm(term, results);
				return term;
			}
		}.transform(term);
	}
	
	static IStrategoTerm  tryConvertAttachmentTypeToTerm( ITermFactory factory , ITermAttachment attachment ){
		try{
			return attachment.getAttachmentType().toTerm(factory, attachment);
		}catch(Exception e){
			//do nothing
		}
		return null;
	}
	public IStrategoTerm fromAnnotations(IStrategoTerm term, final boolean mutableOperations) {
		final TermAttachmentType<?>[] types = TermAttachmentType.getKnownTypes();
		
		return new TermTransformer(factory, true) {
			@Override
			public IStrategoTerm preTransform(IStrategoTerm term) {
				IStrategoTerm origin = OriginAttachment.getOrigin(term); 
				term = fromAnnotations(term, term);
				if (origin != null)
					term = fromAnnotations(origin, term);
				return term;
			}

			private IStrategoTerm fromAnnotations(IStrategoTerm source, IStrategoTerm target) {
				boolean isChanged = false;
				IStrategoList annotations = source.getAnnotations();
				while (!annotations.isEmpty()) {
					IStrategoTerm head = annotations.head();
					if (isTermAppl(head)) {
						IStrategoAppl appl = (IStrategoAppl) head;
						IStrategoConstructor cons = appl.getConstructor();
						TermAttachmentType<?> type = getAttachmentType(cons);
						if (type != null) {
							if (!isChanged) {
								isChanged = true;
								target = removeAttachAnnotations(target, annotations);
							}
							target.putAttachment(type.fromTerm(appl));
						}
					}
					annotations = annotations.tail();
				}
				return target;
			}
			
			private TermAttachmentType<?> getAttachmentType(IStrategoConstructor cons) {
				if (cons == null) return null;
				for (TermAttachmentType<?> type : types) {
					if (type.getTermConstructor() == cons)
						return type;
				}
				return null;
			}

			private IStrategoTerm removeAttachAnnotations(IStrategoTerm term, IStrategoList annotations) {
				List<IStrategoTerm> newAnnos = getNonAttachAnnotations(annotations);
				
				if (mutableOperations && term instanceof StrategoTerm) {
					((StrategoTerm) term).internalSetAnnotations(factory.makeList(newAnnos));
					return term;
				} else {
					return factory.annotateTerm(term, factory.makeList(newAnnos));
				}
			}

			private List<IStrategoTerm> getNonAttachAnnotations(IStrategoList annotations) {
				List<IStrategoTerm> newAnnos = null; 
				while (!annotations.isEmpty()) {
					final IStrategoTerm annotation = annotations.head();
					if (getAttachmentType(tryGetConstructor(annotation)) == null) {
						if (newAnnos == null) newAnnos = new ArrayList<IStrategoTerm>(annotations.size());
						newAnnos.add(annotation);
					}
					annotations = annotations.tail();
				}
				if (newAnnos == null)
					return Collections.emptyList();
				else
					return newAnnos;
			}
		}.transform(term);
	}
}
