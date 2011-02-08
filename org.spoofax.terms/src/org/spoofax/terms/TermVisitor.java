package org.spoofax.terms;

import static org.spoofax.terms.Term.isTermList;

import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;


/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class TermVisitor implements ITermVisitor {
	
	public final boolean visit(IStrategoTerm term) {
		Iterator<IStrategoTerm> iterator = tryGetListIterator(term); 
		for (int i = 0, max = term.getSubtermCount(); i < max; i++) {
			IStrategoTerm child = iterator == null ? term.getSubterm(i) : iterator.next();
			preVisit(child);
			boolean isDone = visit(child);
			postVisit(child);
			if (isDone || isDone(null)) return true;
		}
		return false;
	}

	public static Iterator<IStrategoTerm> tryGetListIterator(IStrategoTerm term) {
		if (isTermList(term)) {
			return StrategoListIterator.iterable((IStrategoList) term).iterator();
		} else {
			return null;
		}
	}
	
	public void postVisit(IStrategoTerm term) {
		// No default implementation
	}
	
	public boolean isDone(IStrategoTerm term) {
		return false;
	}
}

//Local interface avoids abstract method and subsequent @Override annotation requirement

interface ITermVisitor {
	void preVisit(IStrategoTerm node);

	void postVisit(IStrategoTerm node);
	
	boolean isDone(IStrategoTerm node);
}