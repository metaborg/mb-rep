package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * A dummy Stratego term.
 */
public class DummyStrategoTerm extends DummySimpleTerm implements IStrategoTerm {

    @Override
    public IStrategoTerm getSubterm(int i) {
        throw new IndexOutOfBoundsException();
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
        return true;
    }

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
    public String toString(int maxDepth) {
        return "<dummy>";
    }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append("<dummy>");
    }

}
