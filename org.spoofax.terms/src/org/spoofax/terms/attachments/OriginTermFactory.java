package org.spoofax.terms.attachments;

import static org.spoofax.interpreter.terms.IStrategoTerm.MUTABLE;
import static org.spoofax.terms.attachments.OriginAttachment.getOrigin;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

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
	
	public OriginTermFactory(ITermFactory baseFactory) {
		super(MUTABLE, baseFactory);
		assert !(baseFactory instanceof OriginTermFactory);
	}

	public ITermFactory getFactoryWithStorageType(int storageType) {
		assert getDefaultStorageType() <= storageType;
		return this;
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
		
		return (IStrategoAppl) ensureLink(result, oldTerm);
	}
	
	@Override
	public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
		IStrategoTuple result = makeTuple(ensureChildLinks(kids, old), old.getAnnotations());
		return (IStrategoTuple) ensureLink(result, old);
	}
	
	@Override
	public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm origin) {
		if (term.isList()) {
			if (term.getSubtermCount() == origin.getSubtermCount()
					&& origin.isList()) {
				return makeListLink((IStrategoList) term, (IStrategoList) origin);
			} else {
				return term;
			}
		} else if (term.getTermType() == origin.getTermType() && term.getSubtermCount() == origin.getSubtermCount()) {
			ensureChildLinks(term.getAllSubterms(), origin);
			return ensureLink(term, origin);
		} else {
			return ensureLink(term, origin);
		}
	}

	/**
	 * @param origin
	 *            The origin term. For lists, this must be the exact
	 *            corresponding term with the same offset and length.
	 */
	public IStrategoTerm makeLink(IStrategoTerm term, IStrategoTerm origin) {
		assert isOriginRoot(origin);
		if (term.isList()) {
			if (term.getSubtermCount() == origin.getSubtermCount()
				&& origin.isList()) {
				return makeListLink((IStrategoList) term, (IStrategoList) origin);
			}
		} else if (OriginAttachment.get(term) == null) {
			if (term.getStorageType() == MUTABLE) // TODO: handle mutable terms (e.g., some literals)
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
	 * Replaces all subterms in a list,
	 * maintaining only the outer annotations.
	 */
	@Override
	public IStrategoList replaceList(IStrategoTerm[] terms, IStrategoList old) {
		assert terms.length == old.getSubtermCount();
		for (int i = 0; i < terms.length; i++) {
			terms[i] = ensureLink(terms[i], old.head());
			old = old.tail();
		}
		return makeList(terms, old.getAnnotations());
	}
	
	/**
	 * Adds origin tracking information to all subterms of a list.
	 * May add origin tracking information to list Cons nodes.
	 */
	private IStrategoList makeListLink(IStrategoList terms, IStrategoList old) {
		if (terms.isEmpty()) {
			assert old.isEmpty();
			// We don't bother linking empty lists
			return terms;
		} else {
			IStrategoTerm head = terms.head();
			IStrategoList tail = terms.tail();
			IStrategoTerm newHead = ensureLink(head, old.head());
			IStrategoList newTail = makeListLink(tail, old.tail());
			
			/* UNDONE: Origin tracking for Cons nodes
			           (relatively expensive, and who cares about them?)
			if (old instanceof WrappedAstNodeList) {
				WrappedAstNodeList oldList = (WrappedAstNodeList) old;
				return new WrappedAstNodeList(oldList.getNode(), oldList.getOffset(), head, tail, terms.getAnnotations());
			}
			*/
			if (head == newHead && tail == newTail) return terms;
			return makeListCons(newHead, newTail, terms.getAnnotations());
		}
	}
	
	protected IStrategoTerm[] ensureChildLinks(IStrategoTerm[] kids, IStrategoTerm old) {
		assert !old.isList(); // has an expensive getAllSubterms(); shouldn't use this method
		
		IStrategoTerm[] oldKids = old.getAllSubterms();
		if (oldKids == kids) return kids; // no changes; happens with interpreter's all
		for (int i = 0; i < kids.length; i++) {
			kids[i] = ensureLink(kids[i], oldKids[i]);
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
	
	protected IStrategoTerm ensureLink(IStrategoTerm term, IStrategoTerm old) {
		if (isOriginRoot(term)) {
			return term;
		} else {
			IStrategoTerm originRoot = getOriginRoot(old);
			return originRoot == null ? term : makeLink(term, originRoot);
		}
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
		ensureLink(result, term);
		return result;
	}
}
