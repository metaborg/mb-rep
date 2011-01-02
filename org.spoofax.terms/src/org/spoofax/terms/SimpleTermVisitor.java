package org.spoofax.terms;

import java.util.Iterator;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoList;


/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class SimpleTermVisitor implements ISimpleTermVisitor {
	
	public final boolean visit(ISimpleTerm tree) {
		Iterator<ISimpleTerm> iterator = tryGetListIterator(tree); 
		for (int i = 0, max = tree.getSubtermCount(); i < max; i++) {
			ISimpleTerm child = iterator == null ? tree.getSubterm(i) : iterator.next();
			preVisit(child);
			boolean isDone = visit(child);
			postVisit(child);
			if (isDone || isDone()) return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static Iterator<ISimpleTerm> tryGetListIterator(ISimpleTerm tree) {
		boolean isList = tree.isList();
		if (isList) {
			if (tree instanceof Iterable) {
				return ((Iterable<ISimpleTerm>) tree).iterator();
			} else if (tree instanceof IStrategoList) {
				Iterator<?> iterator = StrategoListIterator.iterable((IStrategoList) tree).iterator();
				return (Iterator<ISimpleTerm>) iterator;
			}
		}
		return null;
	}
	
	public void postVisit(ISimpleTerm node) {
		// No default implementation
	}
	
	public boolean isDone() {
		return false;
	}
}

//Local interface avoids abstract method and subsequent @Override annotation requirement

interface ISimpleTermVisitor {
	void preVisit(ISimpleTerm node);

	void postVisit(ISimpleTerm node);
}