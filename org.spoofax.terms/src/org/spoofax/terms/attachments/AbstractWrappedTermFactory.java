package org.spoofax.terms.attachments;

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

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class AbstractWrappedTermFactory extends AbstractTermFactory {
	
	private final ITermFactory baseFactory;
	
	public AbstractWrappedTermFactory(int storageType, ITermFactory baseFactory) {
		super(storageType);
		this.baseFactory = baseFactory.getFactoryWithStorageType(storageType);
		assert checkStorageType(this.baseFactory, storageType);
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
	public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoList annotations) {
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
}
