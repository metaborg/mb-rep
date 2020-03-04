/*
 * Copyright (c) 2005-2012, Karl Trygve Kalleberg <karltk near strategoxt dot org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms.skeleton;

import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.StrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.util.EmptyIterator;
import org.spoofax.terms.util.NotImplementedException;
import org.spoofax.terms.util.TermUtils;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SkeletonStrategoInt extends StrategoTerm implements IStrategoInt {

    private static final long serialVersionUID = 2915870332171452430L;

    private final BigInteger value;

    public SkeletonStrategoInt(long value, IStrategoList annotations) {
        super(annotations);
        this.value = BigInteger.valueOf(value);
    }

    public SkeletonStrategoInt(int value) {
        this(value, null);
    }

    public int intValue() {
        return value.intValue();
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return Collections.emptyList();
    }

    public IStrategoTerm getSubterm(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[0];
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
        if(!TermUtils.isInt(second))
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

}
