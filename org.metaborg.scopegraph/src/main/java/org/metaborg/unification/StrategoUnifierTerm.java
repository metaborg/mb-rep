package org.metaborg.unification;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.AbstractSimpleTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.util.EmptyIterator;

public class StrategoUnifierTerm extends AbstractSimpleTerm implements IStrategoTerm {
    private static final long serialVersionUID = -4729677959138265955L;

    private final StrategoUnifier unifier;

    public StrategoUnifierTerm(StrategoUnifier unifier) {
        this.unifier = unifier;
    }

    public StrategoUnifier unifier() {
        return unifier;
    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public Iterator<IStrategoTerm> iterator() {
        return new EmptyIterator<IStrategoTerm>();
    }

    @Override
    public int getSubtermCount() {
        return 0;
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        throw new NoSuchElementException();
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return new IStrategoTerm[0];
    }

    @Override
    public int getTermType() {
        return BLOB;
    }

    @Override
    public int getStorageType() {
        return MUTABLE;
    }

    @Override
    public IStrategoList getAnnotations() {
        return TermFactory.EMPTY_LIST;
    }

    @Override
    public boolean match(IStrategoTerm second) {
        return equals(second);
    }

    @Override
    public void prettyPrint(ITermPrinter pp) {
        pp.print(unifier.toString());
    }

    @Override
    public String toString(int maxDepth) {
        return unifier.toString();
    }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(toString(maxDepth));
    }

}