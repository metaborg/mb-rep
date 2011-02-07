package org.spoofax.terms.attachments;

import static org.spoofax.interpreter.terms.IStrategoTerm.MUTABLE;
import static org.spoofax.terms.attachments.ParentAttachment.getParent;
import static org.spoofax.terms.attachments.ParentAttachment.setParent;

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
public class ParentTermFactory extends AbstractTermFactory {
	
	// (doesn't implement WrappedTermFactory since it crucially needs
	//  to override any and all new term construction methods)
	
	private final ITermFactory baseFactory;

	public ParentTermFactory(ITermFactory baseFactory) {
		super(MUTABLE);
		assert !(baseFactory instanceof ParentTermFactory);
		this.baseFactory = baseFactory.getFactoryWithStorageType(MUTABLE);
		assert checkStorageType(this.baseFactory, MUTABLE);
	}

	public ITermFactory getFactoryWithStorageType(int storageType) {
		assert getDefaultStorageType() <= storageType;
		return this;
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
		setParent(head, null, result);
		setParent(tail, null, result);
		return result;
	}
	
	protected void configure(IStrategoTerm parent, IStrategoTerm[] kids) {
		for (IStrategoTerm kid : kids)
			setParent(kid, parent, null);
	}

	public void copyAttachments(IStrategoTerm from, IStrategoTerm to, boolean ignoreParentAttachments) {
		if (!ignoreParentAttachments) {
			super.copyAttachments(from, to);
		} else {
			ParentAttachment parent = ParentAttachment.get(to);
			super.copyAttachments(from, to);
			setParent(to, parent);
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

}
