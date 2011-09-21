package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

interface ISymbolResolver<T> 
{	
	public T get(IStrategoString projectName , IStrategoTerm key) throws Exception;
}