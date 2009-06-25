/*
 * Created on 15. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.interpreter.adapter.aterm;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;

import aterm.ATerm;
import aterm.ATermList;

public abstract class WrappedATerm implements IStrategoTerm {

	private final IStrategoList annotations;
	
    protected WrappedATermFactory parent;
    
    protected WrappedATerm(WrappedATermFactory parent, IStrategoList annotations) {
        this.parent = parent;
        this.annotations = annotations;
    }
    
    protected WrappedATerm(WrappedATermFactory parent, ATermList annotations) {
        this(parent, annotations.isEmpty() ? parent.makeList() : (IStrategoList) parent.wrapTerm(annotations));
    }
    
    public int getStorageType() {
        return MAXIMALLY_SHARED;
    }
    
    // FIXME: WrappedATerm.slowCompare does not take annotations into account
    //        (but wrapped aterms don't play nice with other aterms anyway atm)
    
    protected abstract boolean slowCompare(Object second); /*{
        throw new WrapperException("Cannot compare with class " + second.getClass());
    }*/
    
    
    public abstract ATerm getATerm();
    
    public IStrategoList getAnnotations() {
    	return annotations == null ? parent.makeList() : annotations;
    }
    
    protected void internalSetAnnotations(IStrategoList annotations) {
    	
    }
    
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
