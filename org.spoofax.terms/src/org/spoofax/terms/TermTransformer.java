package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.TermType;

/**
 * An abstract, top-down transformation class
 * that can optionally keep all attachments in
 * untransformed tree branches.
 * 
 * Basically works like the topdown(s) strategy.
 */
public abstract class TermTransformer {
	
	private final ITermFactory factory;
	
	private final boolean keepAttachments;
	
	public TermTransformer(ITermFactory factory, boolean keepAttachments) {
		this.factory = factory;
		this.keepAttachments = keepAttachments;
	}

	public final IStrategoTerm transform(IStrategoTerm term) {
		term = preTransform(term);
		return term == null ? null : postTransform(simpleAll(term));
	}
	
	public abstract IStrategoTerm preTransform(IStrategoTerm term);
	
	public IStrategoTerm postTransform(IStrategoTerm term) {
		return term;
	}

	public IStrategoTerm simpleAll(IStrategoTerm current) {
		TermType termType = current.getType();
		IStrategoTerm result;
		
		if (termType == TermType.LIST) {
			result = simpleMapIgnoreAnnos((IStrategoList) current);
		} else {
			IStrategoTerm[] inputs = current.getAllSubterms();
			IStrategoTerm[] results = null;

			for (int i = 0; i < inputs.length; i++) {
				IStrategoTerm arg = inputs[i];
				IStrategoTerm arg2 = transform(arg);
				if (arg2 != arg) {
					if (arg2 == null)
						return null;
					if (results == null)
						results = inputs.clone();
					results[i] = keepAttachments ? factory.copyAttachments(arg, arg2) : arg2;
				}
			}

			if (results == null)
				return current;

			switch (termType) {
				case APPL:
					result = factory.makeAppl(((IStrategoAppl) current).getConstructor(),
							results, current.getAnnotations());
					break;
				case TUPLE:
					result = factory.makeTuple(results, current.getAnnotations());
					break;
				default:
					throw new IllegalStateException("unexpected term type: " + termType + " - " + current);
			}
		}
		
		return keepAttachments ? factory.copyAttachments(current, result) : result;
	}

	private IStrategoList simpleMapIgnoreAnnos(IStrategoList list) {
		IStrategoTerm[] inputs = list.getAllSubterms();
		IStrategoTerm[] results = null;

		for (int i = 0; i < inputs.length; i++) {
			IStrategoTerm arg = inputs[i];
			IStrategoTerm arg2 = transform(arg);
			if (arg2 != arg) {
				if (arg2 == null)
					return null;
				if (results == null)
					results = inputs.clone();
				results[i] = keepAttachments ? factory.copyAttachments(arg, arg2) : arg2;
			}
		}
		
		return results == null ? list : factory.makeList(results, list.getAnnotations());
	}
}