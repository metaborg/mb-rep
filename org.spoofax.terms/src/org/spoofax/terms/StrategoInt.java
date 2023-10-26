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
import org.spoofax.interpreter.terms.TermType;
import org.spoofax.terms.util.EmptyIterator;
import org.spoofax.terms.util.TermUtils;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


public class StrategoInt extends StrategoTerm implements IStrategoInt {

    private static final long serialVersionUID = 2915870332171452430L;

    private final int value;

    public StrategoInt(int value, @Nullable IStrategoList annotations) {
        super(annotations);
        this.value = value;
    }

    public StrategoInt(int value) {
        this(value, null);
    }

    @Override
    public int intValue() {
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
        return TermFactory.EMPTY_TERM_ARRAY;
    }

    @Override
    public int getSubtermCount() {
        return 0;
    }

    @Override
    public TermType getType() {
        return TermType.INT;
    }

    @Override
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
    @Override
    public void prettyPrint(ITermPrinter pp) {
        pp.print(String.valueOf(intValue()));
        printAnnotations(pp);
    }

    @Override
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
