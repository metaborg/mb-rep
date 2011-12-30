/*
 * Created on 15. sep.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

public interface IStrategoInt extends IStrategoTerm {

    public int intValue();
    
    public boolean isUniqueValueTerm();
}
