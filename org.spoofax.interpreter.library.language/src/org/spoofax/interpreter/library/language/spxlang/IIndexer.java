package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;


interface IIndexer 
{	
	public void index(IStrategoString projectName , IStrategoAppl appl) throws Exception ;
}
