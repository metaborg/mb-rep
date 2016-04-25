package org.spoofax.terms.typesmart;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.AbstractWrappedTermFactory;

/**
 * When constructing an application term, this term factory looks for the
 * existence of a type-smart constructor. If such constructor exists, it is used
 * for the construction of the term. Otherwise, a standard build is performed
 * using the base factory.
 * 
 * @author Sebastian Erdweg
 * @author Vlad Vergu
 */
public class TypesmartTermFactory extends AbstractWrappedTermFactory {

	public int checkInvokations = 0;
	public long totalTimeMillis = 0l;

	private final ITermFactory baseFactory;

	private TypesmartLogger logger;

	public TypesmartTermFactory(ITermFactory baseFactory, TypesmartLogger logger) {
		super(baseFactory.getDefaultStorageType(), baseFactory);
		assert baseFactory.getDefaultStorageType() == IStrategoTerm.MUTABLE : "Typesmart factory needs to have a factory with MUTABLE terms";
		this.baseFactory = baseFactory;
		this.logger = logger;
	}

	public ITermFactory getBaseFactory() {
		return baseFactory;
	}

	@Override
	public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm[] kids, IStrategoList annotations) {
		IStrategoAppl term = super.makeAppl(ctr, kids, annotations);

		String[] sorts = checkConstruction(ctr, kids);

		if (sorts == null) {
			String message = "Smart constructor failed for: " + annotateTerm(term, makeList());
			if (logger != null)
				logger.log(message);
			throw new RuntimeException(message);
		}
		else if (sorts.length > 0)
			TypesmartSortAttachment.put(term, sorts);
		
		return term;
	}
	
	/**
	 * @return list of alternative result sorts; null indicates that the construction is illegal; the empty array indicates a special constructor.
	 */
	private String[] checkConstruction(IStrategoConstructor ctr, IStrategoTerm[] kids) {
		checkInvokations++;
		long start = System.currentTimeMillis();
		try {
			String cname = ctr.getName();
			if (cname.equals("") || cname.equals("None") || cname.equals("Cons"))
				return new String[0];
		
			return null;
		} finally {
			long end = System.currentTimeMillis();
			totalTimeMillis += (end - start);
		}
	}

	/**
	 * Identical to
	 * {@link TermFactory#annotateTerm(IStrategoTerm, IStrategoList)} except
	 * that it retains sort attachments.
	 */
	@Override
	public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
		IStrategoTerm result = super.annotateTerm(term, annotations);
		TypesmartSortAttachment attach = TypesmartSortAttachment.get(term);
		if (attach != null)
			TypesmartSortAttachment.put(result, attach);

		return result;
	}

	public ITermFactory getFactoryWithStorageType(int storageType) {
		if (storageType != IStrategoTerm.MUTABLE) 
			throw new RuntimeException("Typesmart factory cannot work with NON-MUTABLE terms");
		
		if (storageType == getDefaultStorageType())
			return this;

		return new TypesmartTermFactory(baseFactory.getFactoryWithStorageType(storageType), logger);
	}

	/**
	 * Recheck invariant of typesmart constrcutor.
	 */
	@Override
	public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old) {
		return makeAppl(constructor, kids, old.getAnnotations());
	}
}
