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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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

    @Override
    public int getArity() {
        return arity;
    }

    @Override
    public String getName() {
        return name;
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
        return TermType.CTOR;
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

    @Override
    public void prettyPrint(ITermPrinter pp) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return name + "`" + arity;
    }

    @Override
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
    @Override
    public IStrategoAppl instantiate(ITermFactory factory, IStrategoTerm... kids) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @Override
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
