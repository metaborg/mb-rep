/*
 * Copyright (c) 2005-2012, Karl Trygve Kalleberg <karltk near strategoxt dot org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.skeleton;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Iterator;

import org.spoofax.EmptyIterator;
import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.TermFactory;

public class SkeletonStrategoInt extends StrategoTerm implements IStrategoInt {

    private static final long serialVersionUID = 2915870332171452430L;
	
    private final BigInteger value;
    
    public SkeletonStrategoInt(long value, IStrategoList annotations, int storageType) {
        super(annotations, storageType);
        this.value = BigInteger.valueOf(value);
    }
    
    public SkeletonStrategoInt(int value, int storageType) {
        this(value, null, storageType);
    }
    
    public int intValue() {
        return value.intValue();
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
    	throw new NotImplementedException();
    }
    
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
    	output.append(Integer.toString(intValue()));
        appendAnnotations(output, maxDepth);
    }
    
    @Override
    public int hashFunction() {
        return 449 * intValue() ^ 7841;
    }
    
	public Iterator<IStrategoTerm> iterator() {
		return new EmptyIterator<IStrategoTerm>();
	}
}
