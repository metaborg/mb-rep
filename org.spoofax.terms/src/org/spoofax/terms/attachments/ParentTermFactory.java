package org.spoofax.terms.attachments;

import static org.spoofax.interpreter.terms.IStrategoTerm.MUTABLE;

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
	
	private final ITermFactory baseFactory;

	public ParentTermFactory(ITermFactory baseFactory) {
		super(MUTABLE);
		assert !(baseFactory instanceof ParentTermFactory);
		this.baseFactory = baseFactory.getFactoryWithStorageType(MUTABLE);
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
		ParentAttachment attachment = new ParentAttachment();
		attachment.setParent(null, result);
		head.putAttachment(attachment);
		tail.putAttachment(attachment);
		return result;
	}
	
	protected void configure(IStrategoTerm parent, IStrategoTerm[] kids) {
		ParentAttachment attachment = new ParentAttachment();
		attachment.setParent(parent, null);
		for (IStrategoTerm kid : kids)
			kid.putAttachment(attachment);
	}

}
