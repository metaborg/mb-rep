package org.spoofax.terms.attachments;

import static org.spoofax.terms.attachments.ParentAttachment.getParent;
import static org.spoofax.terms.attachments.ParentAttachment.putParent;

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
import org.spoofax.terms.StrategoListIterator;
import org.spoofax.terms.StrategoSubList;
import org.spoofax.terms.util.TermUtils;

import javax.annotation.Nullable;

/** 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
@Deprecated // Used only in JSGLR1
public class ParentTermFactory extends AbstractTermFactory {
	
	// (doesn't implement WrappedTermFactory since it crucially needs
	//  to override any and all new term construction methods)
	
	private final ITermFactory baseFactory;

	public ParentTermFactory(ITermFactory baseFactory) {
		super();
		assert !(baseFactory instanceof ParentTermFactory);
		this.baseFactory = baseFactory;
	}

	public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
		IStrategoPlaceholder result = baseFactory.makePlaceholder(template);
		configure(result, template);
		return result;
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

	@Override
	public IStrategoList makeList() {
		return null;
	}

	public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
		// This is a strange case
		// SpoofaxTestingJSGLRI.parseTestedFragments() might depend on it
		IStrategoTerm result = baseFactory.annotateTerm(term, annotations);
		if (TermUtils.isList(term)) {
			for (IStrategoTerm subterm : StrategoListIterator.iterable(TermUtils.toList(term)))
				configure(result, subterm);
		} else {
			configure(result, result.getAllSubterms());
		}
		return result;
	}

	@Override
	public IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoList annotations) {
		IStrategoAppl result = baseFactory.makeAppl(constructor, kids, annotations);
		assert ParentAttachment.get(result) == null :
			"Unexpected parent attachment; doubly wrapped term factory?";
		configure(result, kids);
		return result;
	}

	@Override
	public IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations) {
		IStrategoTuple result = baseFactory.makeTuple(kids, annotations);
		configure(result, kids);
		return result;
	}

	@Override
	public IStrategoList makeList(IStrategoTerm[] kids, IStrategoList annotations) {
		IStrategoList result = baseFactory.makeList(kids, annotations);
		configure(result, kids);
		return result;
	}

	@Override
	public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annos) {
		IStrategoList result = baseFactory.makeListCons(head, tail, annos);
		assert(!(result instanceof StrategoSubList));
		putParent(head, null, result);
		putParent(tail, null, result);
		return result;
	}
	
	protected void configure(IStrategoTerm parent, IStrategoTerm[] kids) {
		for (IStrategoTerm kid : kids)
			putParent(kid, parent, null);
	}
	
	protected void configure(IStrategoTerm parent, IStrategoTerm kid) {
		putParent(kid, parent, null);
	}

	public void copyAttachments(IStrategoTerm from, IStrategoTerm to, boolean ignoreParentAttachments) {
		if (!ignoreParentAttachments) {
			super.copyAttachments(from, to);
		} else {
			ParentAttachment parent = ParentAttachment.get(to);
			super.copyAttachments(from, to);
			putParent(to, parent);
		}
	}

	public IStrategoString tryMakeUniqueString(String name) {
		return baseFactory.tryMakeUniqueString(name);
	}
	
	public static boolean isParentTermFactory(ITermFactory factory) {
		if (factory instanceof ParentTermFactory)
			return true;
		IStrategoTerm i = factory.makeInt(42);
		return getParent(factory.makeTuple(i).getSubterm(0)) != null;
	}

	@Override
	public IStrategoAppl buildAppl(IStrategoConstructor constructor, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		IStrategoAppl newTerm = baseFactory.buildAppl(constructor, subterms, replacee, annotations);
		configure(newTerm, subterms);
		return newTerm;
	}

	@Override
	public IStrategoList buildEmptyList(@Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return baseFactory.buildEmptyList(replacee, annotations);
	}

	@Override
	public IStrategoList buildList(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		IStrategoList newTerm = baseFactory.buildList(subterms, replacee, annotations);
		configure(newTerm, subterms);
		return newTerm;
	}

	@Override
	public IStrategoList buildListConsNil(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		IStrategoList newTerm = baseFactory.buildListConsNil(head, tail, replacee, annotations);
		assert(!(newTerm instanceof StrategoSubList));
		putParent(head, null, newTerm);
		putParent(tail, null, newTerm);
		return newTerm;
	}

	@Override
	public IStrategoTuple buildTuple(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		IStrategoTuple newTerm = baseFactory.buildTuple(subterms, replacee, annotations);
		configure(newTerm, subterms);
		return newTerm;
	}

	@Override
	public IStrategoPlaceholder buildPlaceholder(IStrategoTerm template, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		IStrategoPlaceholder newTerm = baseFactory.buildPlaceholder(template, replacee, annotations);
		configure(newTerm, template);
		return newTerm;
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
	public IStrategoList.Builder createListBuilder(int initialCapacity, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
		return new IStrategoList.Builder() {
			private final IStrategoList.Builder wrappedBuilder = createListBuilder(initialCapacity, replacee, annotations);

			@Override
			public void add(IStrategoTerm term) {
				wrappedBuilder.add(term);
			}

			@Override
			public IStrategoList build() {
				IStrategoList newTerm = wrappedBuilder.build();
				putParent(newTerm.head(), null, newTerm);
				putParent(newTerm.tail(), null, newTerm);
				return newTerm;
			}

			@Override
			public boolean isEmpty() {
				return wrappedBuilder.isEmpty();
			}
		};
	}

	@Override
	public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
		return baseFactory.replaceTerm(term, old);
	}

	@Override
	public IStrategoTerm withAnnotations(IStrategoTerm term, @Nullable IStrategoList annotations) {
		return baseFactory.withAnnotations(term, annotations);
	}

}
