/*
 * Created on 15. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk@ii.uib.no>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.interpreter.adapter.aterm;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;

import aterm.ATerm;

public abstract class WrappedATerm implements IStrategoTerm {

    protected WrappedATermFactory parent;
    
    protected WrappedATerm(WrappedATermFactory parent) {
        this.parent = parent;
    }
    
    protected abstract boolean slowCompare(Object second); /*{
        throw new WrapperException("Cannot compare with class " + second.getClass());
    }*/
    
    
    abstract ATerm getATerm();
    
    public String prettyPrint() {
         throw new NotImplementedException();
    }
    
    public void prettyPrint(ITermPrinter pp) {
        pp.print(getATerm().toString());        
    }
    
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof IStrategoTerm))
            return false;
        return match((IStrategoTerm)obj);
    }
    
    @Override
    public String toString() {
        throw new NotImplementedException();
    }
    
    @Override
    abstract public int hashCode();
}
