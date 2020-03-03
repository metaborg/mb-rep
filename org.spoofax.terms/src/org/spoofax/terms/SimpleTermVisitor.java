package org.spoofax.terms;

import java.util.Iterator;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;


/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class SimpleTermVisitor implements ISimpleTermVisitor {
	
	public final boolean visit(ISimpleTerm term) {
		preVisit(term);
		Iterator<ISimpleTerm> iterator = tryGetListIterator(term); 
		for (int i = 0, max = term.getSubtermCount(); i < max; i++) {
			ISimpleTerm child = iterator == null ? term.getSubterm(i) : iterator.next();
			boolean isDone = visit(child);
			if (isDone || isDone(null)) {
				postVisit(term);
				return true;
			}
		}
		postVisit(term);
		return false;
	}

	@SuppressWarnings("unchecked")
	public static Iterator<ISimpleTerm> tryGetListIterator(ISimpleTerm term) {
		if (term instanceof Iterable) {
			return ((Iterable<ISimpleTerm>) term).iterator();
		} else if (term instanceof IStrategoTerm && TermUtils.isList((IStrategoTerm)term)) {
			Iterator<?> iterator = TermUtils.toList((IStrategoTerm)term).getSubterms().iterator();
			return (Iterator<ISimpleTerm>)iterator;
		}
		return null;
	}
	
	public void postVisit(ISimpleTerm term) {
		// No default implementation
	}
	
	public boolean isDone(ISimpleTerm term) {
		return false;
	}
}

//Local interface avoids abstract method and subsequent @Override annotation requirement

interface ISimpleTermVisitor {
	void preVisit(ISimpleTerm term);

	void postVisit(ISimpleTerm term);
	
	boolean isDone(ISimpleTerm term);
}