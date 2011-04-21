package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndex {

	protected final IStrategoConstructor DEF;

	protected final IStrategoConstructor USE;

	protected final IStrategoConstructor DEF_DATA;

	protected final IStrategoConstructor BAD_DEF;

	protected final IStrategoConstructor BAD_USE;
	
	public SemanticIndex(ITermFactory factory) {
		DEF = factory.makeConstructor("Def", 1);
		USE = factory.makeConstructor("Use", 1);
		DEF_DATA = factory.makeConstructor("DefData", 1);
		BAD_DEF = factory.makeConstructor("BadDef", 1);
		BAD_USE = factory.makeConstructor("BadUse", 1);
	}
	
	public void put(IStrategoAppl entry) {
		IStrategoConstructor type = entry.getConstructor();
		if (type == DEF || type == BAD_DEF) {
			
		} else if (type == USE || type == BAD_USE) {
			
		} else if (type == DEF_DATA) {
			
		} else {
			throw new IllegalArgumentException("Illegal index entry: " + entry);
		}
	}
}
