/*
 * Created on 30. aug.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.interpreter.terms;


public interface IStrategoList extends IStrategoTerm {
	// Too restrictive for client classes: Iterable<IStrategoTerm>

    /**
     * @deprecated Use {@link #getSubterm(int)} instead.
     */
	@Deprecated // useless; only causes incompatibility with other base classes
	public IStrategoTerm get(int index);

    public int size();

    /**
     * @deprecated Use
     *             {@link IStrategoTermBuilder#makeListCons(IStrategoTerm, IStrategoList)}
     *             instead.
     */
    @Deprecated
    public IStrategoList prepend(IStrategoTerm prefix);

    public IStrategoTerm head();

    public IStrategoList tail();

    public boolean isEmpty();

}
