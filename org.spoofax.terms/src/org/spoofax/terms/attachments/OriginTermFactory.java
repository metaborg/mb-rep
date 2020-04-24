package org.spoofax.terms.attachments;

import static org.spoofax.terms.attachments.OriginAttachment.getOrigin;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.StrategoAppl;
import org.spoofax.terms.util.NotImplementedException;
import org.spoofax.terms.util.TermUtils;

/**
 * A factory creating ATerms from AST nodes.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 * @author Karl Trygve Kalleberg <karltk add strategoxt.org>
 */
public abstract class OriginTermFactory extends AbstractWrappedTermFactory {
	
	/**
	 * Whether to reassign the origin for terms that already have an origin.
	 */
	private static boolean REASSIGN_ORIGINS = false;

	/**
	 * Whether to assign new terms as desugared origins
	 */
	private boolean assignDesugaredOrigins = false;

	/**
	 * Sets whether to assign new terms as desugared origins
	 */
	public void setAssignDesugaredOrigins(boolean assignDesugaredOrigins) {
		this.assignDesugaredOrigins = assignDesugaredOrigins;
	}

	public OriginTermFactory(ITermFactory baseFactory) {
		super(baseFactory);
		assert !(baseFactory instanceof OriginTermFactory);
	}
	
	/**
	 * Checks whether the given term is the root of an origin chain.
	 * Only origin roots are tracked in this implementation.
	 * 
	 * Example implementation:
	 * 
	 *   <code>return ImploderAttachment.get(term) != null;</code>
	 */
	public abstract boolean isOriginRoot(IStrategoTerm term);

	@Override
	public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
			IStrategoAppl oldTerm) {
		
		IStrategoList annos = oldTerm.getAnnotations();
		IStrategoAppl result = makeAppl(constructor, ensureChildLinks(kids, oldTerm), annos);
		//TODO: child links only when same signature
		return (IStrategoAppl) ensureLink(result, oldTerm, false);
	}
	
	@Override
	public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
		IStrategoTuple result = makeTuple(ensureChildLinks(kids, old), old.getAnnotations());
		return (IStrategoTuple) ensureLink(result, old, false);
	}
	
	@Override
	public IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail, IStrategoTerm oldHead, IStrategoList oldTail) {
		IStrategoList result = makeListCons(head, tail);
		if (oldHead != head)
			replaceTerm(head, oldHead); // HACK: origin track one level extra in lists...
		return result;
	}
	
	@Override
	public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm origin) {
		if (term == null) {
			return term;
		} else if (term == origin) {
			return term;
		} else if (TermUtils.isList(term)) {
			if (term.getSubtermCount() == origin.getSubtermCount()
					&& TermUtils.isList(origin)) {
				return makeListLink((IStrategoList) term, (IStrategoList) origin);
			} else {
				return term;
			}
		} else if (haveSameSignature(term, origin)) {
			ensureChildLinks(term.getAllSubterms(), origin);
			return ensureLink(term, origin, false);
		} else {
			return ensureLink(term, origin, false);
		}
	}

	private boolean haveSameSignature(IStrategoTerm term, IStrategoTerm origin) {
		if(term instanceof StrategoAppl && origin instanceof StrategoAppl){
			if(!((StrategoAppl)term).getConstructor().equals(((StrategoAppl)origin).getConstructor()))
				return false;
		}
		return 	
			term.getTermType() == origin.getTermType() && 
			term.getSubtermCount() == origin.getSubtermCount();
	}

	/**
	 * @param origin
	 *            The origin term. For lists, this must be the exact
	 *            corresponding term with the same offset and length.
	 */
	public IStrategoTerm makeLink(IStrategoTerm term, IStrategoTerm origin) {
		assert isOriginRoot(origin);
		if (TermUtils.isList(term)) {
			if (term.getSubtermCount() == origin.getSubtermCount()
				&& TermUtils.isList(origin)) {
				return makeListLink((IStrategoList) term, (IStrategoList) origin);
			}
		} else if (OriginAttachment.get(term) == null) {
			OriginAttachment.setOrigin(term, origin);
		} else if (REASSIGN_ORIGINS) {
			throw new NotImplementedException("Not implemented: unwrapping of possibly already wrapped term");
			/*
			StrategoWrapped result = new StrategoWrapped(term);
			setOrigin(result, origin);
			return result;
			*/			
		}
		return term;
	}
	
	/**
	 * @param origin
	 *            The origin term. For lists, this must be the exact
	 *            corresponding term with the same offset and length.
	 */
	public IStrategoTerm makeLinkDesugared(IStrategoTerm term, IStrategoTerm desugared) {
		if (!TermUtils.isList(term) && DesugaredOriginAttachment.get(term) == null) {
			DesugaredOriginAttachment.setDesugaredOrigin(term, desugared);
		} else if (REASSIGN_ORIGINS) {
			throw new NotImplementedException("Not implemented: unwrapping of possibly already wrapped term");
		}
		return term;
	}

	/**
	 * Replaces all subterms in a list,
	 * maintaining only the outer annotations.
	 */
	@Override
	public IStrategoList replaceList(IStrategoTerm[] terms, IStrategoList old) {
		assert terms.length == old.getSubtermCount();
		for (int i = 0; i < terms.length; i++) {
			terms[i] = replaceTerm(terms[i], old.head()); // HACK: origin track one level extra in lists...
			old = old.tail();
		}
		return makeList(terms, old.getAnnotations());
	}
	
	/**
	 * Adds origin tracking information to all subterms of a list.
	 * May add origin tracking information to list Cons nodes.
	 */
	private IStrategoList makeListLink(IStrategoList terms, IStrategoList old) {
		IStrategoList results = terms;
		assert terms.size() == old.size();
		while (!terms.isEmpty()) {
			IStrategoTerm term = terms.head();
			IStrategoTerm oldTerm = old.head();
			IStrategoTerm newTerm = ensureLink(term, oldTerm, false);
			assert newTerm == term : "We assume mutable operations for origins";
			terms = terms.tail();
			old = old.tail();
		}
		return results;
	}
	
	protected IStrategoTerm[] ensureChildLinks(IStrategoTerm[] kids, IStrategoTerm old) {
		assert !TermUtils.isList(old); // has an expensive getAllSubterms(); shouldn't use this method
		
		IStrategoTerm[] oldKids = old.getAllSubterms();
		if (oldKids == kids) return kids; // no changes; happens with interpreter's all
		for (int i = 0; i < kids.length; i++) {
			kids[i] = ensureLink(kids[i], oldKids[i], true);
		}
		return kids;
		/* Before opimization (avoid array copy and exit if kids == oldTerm.getAllSubterms())
		IStrategoTerm[] linkedKids = new IStrategoTerm[kids.length];
		
		for (int i = 0; i < kids.length; i++) {
			linkedKids[i] = ensureLink(kids[i], oldTerm.getSubterm(i));
		}
		return linkedKids;
		*/
	}
	
	protected IStrategoTerm ensureLink(IStrategoTerm term, IStrategoTerm old, boolean isChildLink) {
		if (term == old || isOriginRoot(term)) {
			return term; //TODO: track desugared origins for not origin root lists?
		}		
		if(assignDesugaredOrigins){
			//do not trust child link heuristic
			if(!isChildLink) makeLinkDesugared(term, term);
		} 
		else {
			IStrategoTerm desugared = DesugaredOriginAttachment.getDesugaredOrigin(old);
			if(desugared != null) term = makeLinkDesugared(term, desugared);			
		}
		IStrategoTerm originRoot = getOriginRoot(old);
		return originRoot == null ? term : makeLink(term, originRoot);
	}
	
	protected IStrategoTerm getOriginRoot(IStrategoTerm term) {
		if (isOriginRoot(term)) {
			return term;
		} else {
			IStrategoTerm result = getOrigin(term);
			assert result == null || isOriginRoot(result);
			return result;
		}
	}
	
	@Override
	public IStrategoTerm annotateTerm(IStrategoTerm term,
			IStrategoList annotations) {
		IStrategoTerm result = super.annotateTerm(term, annotations);
		ensureLink(result, term, false);
		return result;
	}
}
