/*
 * Created on 9. okt.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.EmptyIterator;

import java.io.IOException;
import java.util.Iterator;


public class StrategoString extends StrategoTerm implements IStrategoString {

    private static final long serialVersionUID = 237308007762215350L;

    private final String value;

    public StrategoString(String value, IStrategoList annotations) {
        super(annotations);
        this.value = value;
        initImmutableHashCode();
    }

    protected StrategoString(String value) {
        this(value, TermFactory.EMPTY_LIST);
    }

    public IStrategoTerm getSubterm(int index) {
        throw new IndexOutOfBoundsException();
    }

    public IStrategoTerm[] getAllSubterms() {
        return TermFactory.EMPTY;
    }

    public int getSubtermCount() {
        return 0;
    }

    public int getTermType() {
        return IStrategoTerm.STRING;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(second.getTermType() != IStrategoTerm.STRING)
            return false;

        String value = stringValue();
        String secondValue = ((IStrategoString) second).stringValue();

        if(!value.equals(secondValue)) {
            return false;
        }


        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
    }

    public String stringValue() {
        return value;
    }

    public String getName() {
        return value;
    }

    @Deprecated
    public void prettyPrint(ITermPrinter pp) {
        pp.print("\"");
        pp.print(stringValue().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"));
        pp.print("\"");
        printAnnotations(pp);
    }

    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append("\"");
        output.append(
            stringValue().replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r"));
        output.append("\"");
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        return stringValue().hashCode();
    }

    public Iterator<IStrategoTerm> iterator() {
        return new EmptyIterator<IStrategoTerm>();
    }
}
