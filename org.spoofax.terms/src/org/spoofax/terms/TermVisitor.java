package org.spoofax.terms;

import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.util.TermUtils;


/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class TermVisitor implements ITermVisitor {
	
	public final boolean visit(IStrategoTerm term) {
		preVisit(term);
		Iterator<IStrategoTerm> iterator = tryGetListIterator(term); 
		for (int i = 0, max = term.getSubtermCount(); i < max; i++) {
			IStrategoTerm child = iterator == null ? term.getSubterm(i) : iterator.next();
			boolean isDone = visit(child);
			if (isDone || isDone(null)) {
				postVisit(term);
				return true;
			}
		}
		postVisit(term);
		return false;
	}

	public static Iterator<IStrategoTerm> tryGetListIterator(IStrategoTerm term) {
		if (TermUtils.isList(term)) {
			return StrategoListIterator.iterable(TermUtils.toList(term)).iterator();
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
	void preVisit(IStrategoTerm term);

	void postVisit(IStrategoTerm term);
	
	boolean isDone(IStrategoTerm term);
}