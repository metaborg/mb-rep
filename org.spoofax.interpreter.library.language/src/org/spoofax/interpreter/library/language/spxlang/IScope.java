package org.spoofax.interpreter.library.language.spxlang;

interface IScope {
    public String getScopeName();

    public Scope getEnclosingScope();

    public void define(SpxSymbol sym);
    
    public SpxSymbol resolve(String name);

}

interface ISpxType {
	
	public String getName();
}
