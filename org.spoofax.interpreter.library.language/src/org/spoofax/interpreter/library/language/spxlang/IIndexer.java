package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;


interface IIndexer 
{	
	public void index(IStrategoString projectName , IStrategoAppl appl) throws Exception ;
}

interface IIndexManageCommand
{
	public void executeCommnad(IStrategoTerm projectName , Object... objects) throws Exception;
}
