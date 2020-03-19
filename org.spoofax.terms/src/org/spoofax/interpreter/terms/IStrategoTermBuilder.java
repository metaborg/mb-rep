/*
 * Created on 08.aug.2005
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;

import java.util.Collection;


public interface IStrategoTermBuilder {
    public IStrategoConstructor makeConstructor(String string, int arity);
    
    // @Deprecated public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids);
    public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... terms);

    public IStrategoPlaceholder makePlaceholder(IStrategoTerm template);
    public IStrategoInt makeInt(int i);
    public IStrategoReal makeReal(double d);
    public IStrategoTuple makeTuple(IStrategoTerm... terms);
    public IStrategoString makeString(String s);
    public IStrategoList makeList();
    public IStrategoList makeList(IStrategoTerm... terms);
    public IStrategoList makeList(Collection<? extends IStrategoTerm> terms);

	public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoList annotations);
	public IStrategoList makeList(IStrategoTerm[] kids, IStrategoList annotations);
	public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annotations);
	public IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations);

    // @Deprecated public IStrategoList makeList(IStrategoTerm head, IStrategoList tail);
    public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail);
    
    public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations);
    
    public IStrategoString tryMakeUniqueString(String name);
    
    public IStrategoTerm copyAttachments(IStrategoTerm from, IStrategoTerm to);

    default IStrategoAppl makeAppl(String cons, IStrategoTerm... children) {
        return makeAppl(makeConstructor(cons, children.length), children, null);
    }

    IStrategoList.Builder arrayListBuilder();
    IStrategoList.Builder arrayListBuilder(int initialCapacity);
    default IStrategoList makeList(IStrategoList.Builder builder) {
        return builder.build();
    }
}


