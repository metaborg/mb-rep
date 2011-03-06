/*
 * Created on 28. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.terms;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;


public class StrategoInt extends StrategoTerm implements IStrategoInt {

    private final int value;
    
    protected StrategoInt(int value, IStrategoList annotations, int storageType) {
        super(annotations, storageType);
        this.value = value;
    }
    
    protected StrategoInt(int value, int storageType) {
        this(value, null, storageType);
    }
    
    public int intValue() {
        return value;
    }

    public IStrategoTerm[] getAllSubterms() {
        return TermFactory.EMPTY;
    }

    public IStrategoTerm getSubterm(int index) {
        throw new IndexOutOfBoundsException();
    }

    public int getSubtermCount() {
        return 0;
    }

    public int getTermType() {
        return IStrategoTerm.INT;
    }
    
    public boolean isUniqueValueTerm() {
    	return false;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
        if(second.getTermType() != IStrategoTerm.INT)
            return false;

        if (intValue() != ((IStrategoInt) second).intValue())
        	return false;

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if (annotations == secondAnnotations) {
        	return true;
        } else if (annotations.match(secondAnnotations)) {
        	if (commonStorageType == SHARABLE) internalSetAnnotations(secondAnnotations);
        	return true;
        } else {
        	return false;
        }
    }

    @Deprecated
	public void prettyPrint(ITermPrinter pp) {
        pp.print(String.valueOf(intValue()));
        printAnnotations(pp);
    }
    
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
    	output.append(Integer.toString(intValue()));
        appendAnnotations(output, maxDepth);
    }
    
    @Override
    public int hashFunction() {
        return 449 * intValue() ^ 7841;
    }
}
