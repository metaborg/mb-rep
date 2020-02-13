/*
 * Created on 28. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 *
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import org.spoofax.interpreter.terms.*;
import org.spoofax.terms.util.EmptyIterator;

import java.io.IOException;
import java.util.Iterator;

public final class StrategoConstructor extends StrategoTerm implements IStrategoConstructor {

    private static final long serialVersionUID = -4477361122406081825L;

    private final String name;

    private final int arity;

    public StrategoConstructor(String name, int arity) {
        super(null);
        this.name = name;
        this.arity = arity;
        if(name == null)
            throw new IllegalArgumentException("name cannot be null");
    }

    public int getArity() {
        return arity;
    }

    public String getName() {
        return name;
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

    public final int getTermType() {
        return IStrategoTerm.CTOR;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(second == this)
            return true;
        if(second == null || second.getTermType() != CTOR)
            return false;

        IStrategoConstructor other = (IStrategoConstructor) second;

        return name.equals(other.getName()) && arity == other.getArity();
    }

    public void prettyPrint(ITermPrinter pp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return name + "`" + arity;
    }

    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(name);
        output.append('`');
        output.append(Integer.toString(arity));
    }

    public IStrategoConstructor getConstructor() {
        return this;
    }

    @Override
    public int hashFunction() {
        // TODO: hash code that is reproducible from Stratego
        return name.hashCode() + 5407 * arity;
    }

    @Deprecated
    public IStrategoAppl instantiate(ITermFactory factory, IStrategoTerm... kids) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public IStrategoAppl instantiate(ITermFactory factory, IStrategoList kids) {
        throw new UnsupportedOperationException();
    }

    public Iterator<IStrategoTerm> iterator() {
        return new EmptyIterator<IStrategoTerm>();
    }

    private Object readResolve() {
        IStrategoConstructor cachedConstructor = AbstractTermFactory.createCachedConstructor(name, arity);
        return cachedConstructor;
    }
}
