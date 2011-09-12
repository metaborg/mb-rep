package org.spoofax.interpreter.library.language.spxlang;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoTerm;

interface IScope {
    
	public String getScopeName();

    public ScopeIdentifier getEnclosingScope();

    public void define(SpxSymbol sym);
    
    public SpxSymbol resolve(String name);
}

interface ISpxType {
	
	public String getName();
}


interface Scope 
{
	public IStrategoTerm getScopeId();
	
	public Scope getEnclosingScope();
	
	public IStrategoTerm getType();
	
	public URI getScopeURI();
	
	//TODO : Add Scope specific Define and Resolve 
	//
}