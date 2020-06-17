/*
 * Created on 08.aug.2005
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

import java.util.Collection;

// TODO: This interface is never used directly and should be removed
public interface IStrategoTermBuilder {
    public IStrategoConstructor makeConstructor(String string, int arity);
    
    // @Deprecated public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids);
    /** @deprecated Use {@link ITermFactory#buildAppl} instead. */
    @Deprecated
    public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... terms);

    /** @deprecated Use {@link ITermFactory#buildPlaceholder} instead. */
    @Deprecated
    public IStrategoPlaceholder makePlaceholder(IStrategoTerm template);

    /** @deprecated Use {@link ITermFactory#buildInt} instead. */
    @Deprecated
    public IStrategoInt makeInt(int i);

    /** @deprecated Use {@link ITermFactory#buildReal} instead. */
    @Deprecated
    public IStrategoReal makeReal(double d);

    /** @deprecated Use {@link ITermFactory#buildTuple} instead. */
    @Deprecated
    public IStrategoTuple makeTuple(IStrategoTerm... terms);

    /** @deprecated Use {@link ITermFactory#buildString} instead. */
    @Deprecated
    public IStrategoString makeString(String s);

    /** @deprecated Use {@link ITermFactory#buildEmptyList} instead. */
    @Deprecated
    public IStrategoList makeList();

    /** @deprecated Use {@link ITermFactory#buildList} instead. */
    @Deprecated
    public IStrategoList makeList(IStrategoTerm... terms);

    /** @deprecated Use {@link ITermFactory#buildList} instead. */
    @Deprecated
    public IStrategoList makeList(Collection<? extends IStrategoTerm> terms);

    /** @deprecated Use {@link ITermFactory#buildAppl} instead. */
    @Deprecated
	public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoList annotations);

    /** @deprecated Use {@link ITermFactory#buildList} instead. */
    @Deprecated
	public IStrategoList makeList(IStrategoTerm[] kids, IStrategoList annotations);

    /** @deprecated Use {@link ITermFactory#buildListConsNil} instead. */
    @Deprecated
	public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annotations);

    /** @deprecated Use {@link ITermFactory#buildTuple} instead. */
    @Deprecated
	public IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations);

    // @Deprecated public IStrategoList makeList(IStrategoTerm head, IStrategoList tail);
    /** @deprecated Use {@link ITermFactory#buildListConsNil} instead. */
    @Deprecated
    public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail);

    /** @deprecated Use {@link ITermFactory#withAnnotations} instead. */
    @Deprecated
    public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations);
    
    public IStrategoString tryMakeUniqueString(String name);

    /** Note: in most cases, use {@link ITermFactory#replaceTerm} instead. */
    public IStrategoTerm copyAttachments(IStrategoTerm from, IStrategoTerm to);

    /** @deprecated Use {@link ITermFactory#buildAppl} instead. */
    @Deprecated
    default IStrategoAppl makeAppl(String cons, IStrategoTerm... children) {
        return makeAppl(makeConstructor(cons, children.length), children, null);
    }

    /** @deprecated Use {@link ITermFactory#createListBuilder} instead. */
    @Deprecated
    IStrategoList.Builder arrayListBuilder();

    /** @deprecated Use {@link ITermFactory#createListBuilder} instead. */
    @Deprecated
    IStrategoList.Builder arrayListBuilder(int initialCapacity);

    /** @deprecated Use {@link ITermFactory#createListBuilder} and {@link IStrategoList.Builder#build()} instead. */
    @Deprecated
    default IStrategoList makeList(IStrategoList.Builder builder) {
        return builder.build();
    }
}


