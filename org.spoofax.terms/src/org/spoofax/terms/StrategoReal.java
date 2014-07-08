/*
 * Created on 28. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import java.io.IOException;
import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.EmptyIterator;


public class StrategoReal extends StrategoTerm implements IStrategoReal {

    private static final long serialVersionUID = 9005617684098182139L;
	
    private final double value;
    
    public StrategoReal(double value, IStrategoList annotations, int storageType) {
        super(annotations, storageType);
        this.value = value;
    }
    
    protected StrategoReal(double value, int storageType) {
        this(value, null, storageType);
    }
    
    public double realValue() {
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
        return IStrategoTerm.REAL;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
        if(second.getTermType() != IStrategoTerm.REAL)
            return false;

        if (realValue() != ((IStrategoReal) second).realValue())
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
        pp.print("" + realValue());
        printAnnotations(pp);
    }
    
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
    	output.append(Double.toString(realValue()));
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        return (int)(449 * value) ^ 7841;
    }

	public Iterator<IStrategoTerm> iterator() {
		return new EmptyIterator<IStrategoTerm>();
	}
}
