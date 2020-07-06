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

import javax.annotation.Nullable;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class AbstractWrappedTermFactory extends AbstractTermFactory {
	
	private final ITermFactory baseFactory;
	
	public AbstractWrappedTermFactory(ITermFactory baseFactory) {
		this.baseFactory = baseFactory;
	}

	@Override public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
		return baseFactory.makePlaceholder(template);
	}

	@Override public IStrategoInt makeInt(int i) {
		return baseFactory.makeInt(i);
	}

	@Override public IStrategoReal makeReal(double d) {
		return baseFactory.makeReal(d);
	}

	@Override public IStrategoString makeString(String s) {
		return baseFactory.makeString(s);
	}

	@Override public IStrategoTerm annotateTerm(IStrategoTerm term, @Nullable IStrategoList annotations) {
		return baseFactory.annotateTerm(term, annotations);
	}

	@Override public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, @Nullable IStrategoList annotations) {
		return baseFactory.makeAppl(constructor, kids, annotations);
	}

	@Override public IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations) {
		return baseFactory.makeTuple(kids, annotations);
	}

	@Override public IStrategoList makeList() {
		return baseFactory.makeList();
	}

	@Override public IStrategoList makeList(IStrategoTerm[] kids, @Nullable IStrategoList annotations) {
		return baseFactory.makeList(kids, annotations);
	}

	@Override public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoList annos) {
		return baseFactory.makeListCons(head, tail, annos);
	}

	public IStrategoString tryMakeUniqueString(String name) {
		return baseFactory.tryMakeUniqueString(name);
	}

	@Override public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
		return baseFactory.replaceTerm(term, old);
	}



}
