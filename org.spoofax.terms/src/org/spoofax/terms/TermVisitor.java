package org.spoofax.terms;

import static org.spoofax.terms.Term.isTermList;

import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;


/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class TermVisitor implements ITermVisitor {
	
	public final boolean visit(IStrategoTerm tree) {
		Iterator<IStrategoTerm> iterator = tryGetListIterator(tree); 
		for (int i = 0, max = tree.getSubtermCount(); i < max; i++) {
			IStrategoTerm child = iterator == null ? tree.getSubterm(i) : iterator.next();
			preVisit(child);
			boolean isDone = visit(child);
			postVisit(child);
			if (isDone || isDone()) return true;
		}
		return false;
	}

	public static Iterator<IStrategoTerm> tryGetListIterator(IStrategoTerm tree) {
		if (isTermList(tree)) {
			return StrategoListIterator.iterable((IStrategoList) tree).iterator();
		} else {
			return null;
		}
	}
	
	public void postVisit(IStrategoTerm node) {
		// No default implementation
	}
	
	public boolean isDone() {
		return false;
	}
}

//Local interface avoids abstract method and subsequent @Override annotation requirement

interface ITermVisitor {
	void preVisit(IStrategoTerm node);

	void postVisit(IStrategoTerm node);
}