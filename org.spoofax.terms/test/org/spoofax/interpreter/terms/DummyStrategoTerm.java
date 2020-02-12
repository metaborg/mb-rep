package org.spoofax.interpreter.terms;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;


/**
 * A dummy Stratego term.
 */
/* package private */ class DummyStrategoTerm extends DummySimpleTerm implements IStrategoTerm {

    @Override
    public IStrategoTerm getSubterm(int i) { throw new IndexOutOfBoundsException(); }

    @Override
    public IStrategoTerm[] getAllSubterms() { return new IStrategoTerm[0]; }

    @Override
    public int getTermType() { return 9; }

    @Override
    public IStrategoList getAnnotations() { throw new IllegalStateException("Not supported."); }

    @Override
    public boolean match(IStrategoTerm second) { return true; }

    @Override
    public void prettyPrint(ITermPrinter pp) {
        try {
            pp.append("<dummy>");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object obj) {
        // Identity
        return this == obj;
    }

    @Override
    public int hashCode() {
        // Identity
        return System.identityHashCode(this);
    }

    @Override
    public String toString(int maxDepth) { return "<dummy>"; }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException { output.append("<dummy>"); }

    @Override
    public Iterator<IStrategoTerm> iterator() { return Arrays.asList(getAllSubterms()).iterator(); }

}
