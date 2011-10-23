package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * An artificial partial list AST node.
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class StrategoSubList extends StrategoWrapped implements IStrategoList {


	private final IStrategoList completeList;

	private int indexStart;

	private int indexEnd;
	
	public StrategoSubList(IStrategoList completeList, IStrategoList wrapped, int indexStart, int indexEnd) {
		super(wrapped);
		this.completeList = completeList;
		this.indexStart = indexStart;
		this.indexEnd = indexEnd;
	}

	public IStrategoList getCompleteList() {
		return completeList;
	}

	public int getIndexStart() {
		return indexStart;
	}

	public int getIndexEnd() {
		return indexEnd;
	}

	public IStrategoTerm getFirstChild() {
		return getSubterm(0);
	}

	public IStrategoTerm getLastChild() {
		return getSubterm(getSubtermCount()-1);
	}
}
