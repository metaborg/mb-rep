/*
 * Created on 28. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.EmptyIterator;
import org.spoofax.terms.util.TermUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class StrategoReal extends StrategoTerm implements IStrategoReal {

    private static final long serialVersionUID = 9005617684098182139L;

    private final double value;

    public StrategoReal(double value, IStrategoList annotations) {
        super(annotations);
        this.value = value;
    }

    protected StrategoReal(double value) {
        this(value, null);
    }

    @Override
    public double realValue() {
        return value;
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return Collections.emptyList();
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[0];
    }

    @Override
    public int getSubtermCount() {
        return 0;
    }

    @Override
    public int getTermType() {
        return IStrategoTerm.REAL;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(!TermUtils.isReal(second))
            return false;

        if(realValue() != ((IStrategoReal) second).realValue())
            return false;

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
    }

    @Deprecated
    @Override
    public void prettyPrint(ITermPrinter pp) {
        pp.print("" + realValue());
        printAnnotations(pp);
    }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(Double.toString(realValue()));
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        return (int) (449 * value) ^ 7841;
    }

    public Iterator<IStrategoTerm> iterator() {
        return new EmptyIterator<IStrategoTerm>();
    }
}
