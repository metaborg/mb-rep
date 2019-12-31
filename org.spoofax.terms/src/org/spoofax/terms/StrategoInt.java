/*
 * Created on 28. jan.. 2007
 *
 * Copyright (c) 2005-2012, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.EmptyIterator;

import java.io.IOException;
import java.util.Iterator;


public class StrategoInt extends StrategoTerm implements IStrategoInt {

    private static final long serialVersionUID = 2915870332171452430L;

    private final int value;

    public StrategoInt(int value, IStrategoList annotations) {
        super(annotations);
        this.value = value;
    }

    public StrategoInt(int value) {
        this(value, null);
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
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(second.getTermType() != IStrategoTerm.INT)
            return false;

        if(intValue() != ((IStrategoInt) second).intValue())
            return false;

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
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

    public Iterator<IStrategoTerm> iterator() {
        return new EmptyIterator<IStrategoTerm>();
    }
}
