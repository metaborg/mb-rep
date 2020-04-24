package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.TermFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A dummy Stratego term.
 */
public class DummyStrategoTerm extends DummySimpleTerm implements IStrategoTerm {

    public DummyStrategoTerm() {
        super();
    }

    public DummyStrategoTerm(String name) {
        super(name);
    }

    @Override
    public IStrategoTerm getSubterm(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return TermFactory.EMPTY_TERM_ARRAY;
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return Collections.emptyList();
    }

    @Override
    public int getTermType() {
        return 9;
    }

    @Override
    public IStrategoList getAnnotations() {
        throw new IllegalStateException("Not supported.");
    }

    @Override
    public boolean match(IStrategoTerm second) {
        // Identity
        return this == second;
    }

    @Override
    public void prettyPrint(ITermPrinter pp) {
        try {
            pp.append(this.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof IStrategoTerm && match((IStrategoTerm)obj);
    }

    @Override
    public String toString(int maxDepth) {
        return this.toString();
    }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(this.toString());
    }

    @Override
    public Iterator<IStrategoTerm> iterator() {
        return Arrays.asList(getAllSubterms()).iterator();
    }

}
