package org.spoofax.terms.attachments;

import java.util.Collection;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.AbstractTermFactory;
import org.spoofax.terms.ParseError;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class AbstractWrappedTermFactory extends AbstractTermFactory {

	private ITermFactory baseFactory;

	public AbstractWrappedTermFactory(int storageType, ITermFactory baseFactory) {
		super(storageType);
		replaceBaseFactory(baseFactory);
	}

	/**
	 * Shallow operation of {@link #replaceBaseFactory(ITermFactory, boolean)}
	 * 
	 */
	public ITermFactory replaceBaseFactory(ITermFactory baseFactory) {
		return replaceBaseFactory(baseFactory, false);
	}

	/**
	 * Replace the deepest base factory with the given one returning the replaced base factory/
	 * 
	 * @param baseFactory
	 *            The new base factory
	 * @param deep
	 *            If <code>true</code> then the deepest possible base factory will be replaced. If
	 *            <code>false</code> then the immediate base factory will be replaced
	 * 
	 * @return The replaced base factory
	 */
	public ITermFactory replaceBaseFactory(ITermFactory baseFactory, boolean deep) {
		if (deep && this.baseFactory instanceof AbstractWrappedTermFactory) {
			return ((AbstractWrappedTermFactory) this.baseFactory).replaceBaseFactory(baseFactory);
		}
		ITermFactory oldFactory = this.baseFactory;
		this.baseFactory = baseFactory;
		assert checkStorageType(this.baseFactory, getDefaultStorageType());
		return oldFactory;
	}

	public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
		return baseFactory.makePlaceholder(template);
	}

	public IStrategoInt makeInt(int i) {
		return baseFactory.makeInt(i);
	}

	public IStrategoReal makeReal(double d) {
		return baseFactory.makeReal(d);
	}

	public IStrategoString makeString(String s) {
		return baseFactory.makeString(s);
	}

	public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
		return baseFactory.annotateTerm(term, annotations);
	}

	@Override
	public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annos) {
		return baseFactory.makeListCons(head, tail, annos);
	}

	public IStrategoString tryMakeUniqueString(String name) {
		return baseFactory.tryMakeUniqueString(name);
	}

	@Override
	public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
		return baseFactory.replaceTerm(term, old);
	}

	/**
	 * Shallow operation of {@link #getWrappedFactory(boolean)}
	 * 
	 * @return
	 */
	public ITermFactory getWrappedFactory() {
		return getWrappedFactory(false);
	}

	/**
	 * Returns the wrapped factory. If the argument is <code>true</code> then an attempt is made to
	 * return the deepest base factory.
	 * 
	 * @param deep
	 *            <code>true</code> if the deepest base factory should be returned
	 * @return
	 */
	public ITermFactory getWrappedFactory(boolean deep) {
		if (deep && this.baseFactory instanceof AbstractWrappedTermFactory) {
			return ((AbstractWrappedTermFactory) this.baseFactory).getWrappedFactory(true);
		}
		return baseFactory;
	}

	@Override
	public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
			IStrategoList annotations) {
		return baseFactory.makeAppl(constructor, kids, annotations);
	}

	@Override
	public IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations) {
		return baseFactory.makeTuple(kids, annotations);
	}

	@Override
	public IStrategoList makeList(IStrategoTerm[] kids, IStrategoList annotations) {
		return baseFactory.makeList(kids, annotations);
	}

	@Override
	public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
			IStrategoAppl old) {
		return baseFactory.replaceAppl(constructor, kids, old);
	}

	@Override
	public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
		return baseFactory.replaceTuple(kids, old);
	}

	@Override
	public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old) {
		return baseFactory.replaceList(kids, old);
	}

	@Override
	public IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail,
			IStrategoTerm oldHead, IStrategoList oldTail) {
		return baseFactory.replaceListCons(head, tail, oldHead, oldTail);
	}

	@Override
	public IStrategoList makeList() {
		return baseFactory.makeList();
	}

	@Override
	public IStrategoList makeList(Collection<? extends IStrategoTerm> terms) {
		return baseFactory.makeList(terms);
	}

	@Override
	public IStrategoTerm parseFromString(String text) throws ParseError {
		return baseFactory.parseFromString(text);
	}

	@Override
	public IStrategoTerm copyAttachments(IStrategoTerm from, IStrategoTerm to) {
		return baseFactory.copyAttachments(from, to);
	}
}
