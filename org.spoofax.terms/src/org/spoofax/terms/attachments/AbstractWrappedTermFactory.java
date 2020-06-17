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
import org.spoofax.terms.*;
import org.spoofax.terms.util.TermUtils;

import javax.annotation.Nullable;
import java.util.Collection;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public abstract class AbstractWrappedTermFactory extends AbstractTermFactory {
	
	private final ITermFactory baseFactory;
	
	public AbstractWrappedTermFactory(ITermFactory baseFactory) {
		this.baseFactory = baseFactory;
	}

	@Override
	@Deprecated
	public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
		return baseFactory.makePlaceholder(template);
	}

	@Override
	@Deprecated
	public IStrategoInt makeInt(int i) {
		return baseFactory.makeInt(i);
	}

	@Override
	@Deprecated
	public IStrategoReal makeReal(double d) {
		return baseFactory.makeReal(d);
	}

	@Override
	@Deprecated
	public IStrategoString makeString(String s) {
		return baseFactory.makeString(s);
	}

	@Override
	@Deprecated
	public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
		return baseFactory.annotateTerm(term, annotations);
	}

	@Override
	@Deprecated
	public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoList annotations) {
		return baseFactory.makeAppl(constructor, kids, annotations);
	}

	@Override
	@Deprecated
	public IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations) {
		return baseFactory.makeTuple(kids, annotations);
	}

	@Override
	@Deprecated
	public IStrategoList makeList() {
		return baseFactory.makeList();
	}

	@Override
	@Deprecated
	public IStrategoList makeList(IStrategoTerm[] kids, IStrategoList annotations) {
		return baseFactory.makeList(kids, annotations);
	}

	@Override
	@Deprecated
	public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annos) {
		return baseFactory.makeListCons(head, tail, annos);
	}

	public IStrategoString tryMakeUniqueString(String name) {
		return baseFactory.tryMakeUniqueString(name);
	}

	@Override
	@Deprecated
	public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
		return baseFactory.replaceTerm(term, old);
	}



	@Override
	public IStrategoAppl buildAppl(String constructorName, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildAppl(constructorName, subterms, replacee, annotations);
	}

	@Override
	public IStrategoAppl buildAppl(String constructorName, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildAppl(constructorName, subterms, replacee, annotations);
	}

	@Override
	public IStrategoAppl buildAppl(IStrategoConstructor constructor, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildAppl(constructor, subterms, replacee, annotations);
	}

	@Override
	public IStrategoAppl buildAppl(IStrategoConstructor constructor, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildAppl(constructor, subterms, replacee, annotations);
	}

	@Override
	public IStrategoList buildEmptyList(@Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildEmptyList(replacee, annotations);
	}

	@Override
	public IStrategoList buildList(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildList(subterms, replacee, annotations);
	}

	@Override
	public IStrategoList buildList(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildList(subterms, replacee, annotations);
	}

	@Override
	public IStrategoList buildListConsNil(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildListConsNil(head, tail, replacee, annotations);
	}

	@Override
	public IStrategoTuple buildTuple(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildTuple(subterms, replacee, annotations);
	}

	@Override
	public IStrategoTuple buildTuple(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildTuple(subterms, replacee, annotations);
	}

	@Override
	public IStrategoPlaceholder buildPlaceholder(IStrategoTerm template, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildPlaceholder(template, replacee, annotations);
	}

	@Override
	public IStrategoInt buildInt(int value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildInt(value, replacee, annotations);
	}

	@Override
	public IStrategoReal buildReal(double value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildReal(value, replacee, annotations);
	}

	@Override
	public IStrategoString buildString(String value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildString(value, replacee, annotations);
	}

	@Override
	public IStrategoTerm withAnnotations(IStrategoTerm term, @Nullable IStrategoList annotations) {
		return baseFactory.withAnnotations(term, annotations);
	}

	@Override
	public IStrategoList.Builder createListBuilder(int initialCapacity, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.createListBuilder(initialCapacity, replacee, annotations);
	}
}
