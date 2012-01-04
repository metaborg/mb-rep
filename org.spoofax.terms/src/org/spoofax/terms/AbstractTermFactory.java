package org.spoofax.terms;

import static java.lang.Math.min;
import static org.spoofax.interpreter.terms.IStrategoTerm.MAXIMALLY_SHARED;
import static org.spoofax.interpreter.terms.IStrategoTerm.MUTABLE;

import java.util.Collection;
import java.util.HashMap;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.ITermAttachment;

public abstract class AbstractTermFactory implements ITermFactory {

	@Deprecated
	public static final IStrategoList EMPTY_LIST = new StrategoList(null, null, null, IStrategoTerm.MAXIMALLY_SHARED);

    public static final IStrategoTerm[] EMPTY = new IStrategoTerm[0];
    
    private final StringTermReader reader = new StringTermReader(this);

    private static final HashMap<StrategoConstructor, StrategoConstructor> asyncCtorCache =
        new HashMap<StrategoConstructor, StrategoConstructor>();
    
    protected int defaultStorageType;
    
    public AbstractTermFactory(int defaultStorageType) {
		this.defaultStorageType = defaultStorageType;
	}
    
    public final int getDefaultStorageType() {
		return defaultStorageType;
	}
    
    protected final boolean isTermSharingAllowed() {
    	return defaultStorageType != MUTABLE;
    }

    public StrategoConstructor makeConstructor(String name, int arity) {
        StrategoConstructor result = new StrategoConstructor(name, arity);
        synchronized (TermFactory.class) {
	        StrategoConstructor cached = asyncCtorCache.get(result);
	        if (cached == null) {
	            asyncCtorCache.put(result, result);
	        } else {
	            result = cached;
	        }
        }
        return result;
    }

    public abstract IStrategoAppl makeAppl(IStrategoConstructor constructor,
			IStrategoTerm[] kids, IStrategoList annotations);

    public abstract IStrategoTuple makeTuple(
			IStrategoTerm[] kids, IStrategoList annotations);

    public abstract IStrategoList makeList(
			IStrategoTerm[] kids, IStrategoList annotations);

    public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
            IStrategoAppl old) {
        return makeAppl(constructor, kids, old.getAnnotations());
    }

	public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
        return makeTuple(kids, old.getAnnotations());
    }
    
    public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old) {
        return makeList(kids, old.getAnnotations());
    }
    
    public IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail, IStrategoTerm oldHead, IStrategoList oldTail) {
        return makeListCons(head, tail);
    }
    
    public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
    	return term;
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids,
            IStrategoList annotations) {
        return makeAppl(ctr, kids.getAllSubterms(), annotations);
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids) {
        return makeAppl(ctr, kids, null);
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... terms) {
        return makeAppl(ctr, terms, null);
    }

    public final IStrategoList makeList(IStrategoTerm... terms) {
        return makeList(terms, null);
    }

    public IStrategoList makeList() {
        return makeList(EMPTY, null);
    }

    public IStrategoList makeList(Collection<? extends IStrategoTerm> terms) {
        return makeList(terms.toArray(new IStrategoTerm[terms.size()]));
    }

    public final IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail) {
        return makeListCons (head, tail, null);
    }

    public abstract IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annos);

	public final IStrategoTuple makeTuple(IStrategoTerm... terms) {
        return makeTuple(terms, null);
    }
    
    public IStrategoTerm parseFromString(String text) throws ParseError {
    	return reader.parseFromString(text);
    }
    
    protected static int getStorageType(IStrategoTerm term) {
    	return term == null ? MAXIMALLY_SHARED : term.getStorageType();
    }
    
    protected static int getStorageType(IStrategoTerm term1, IStrategoTerm term2) {
    	int result = term1.getStorageType();
    	if (result == 0) return 0;
    	return min(result, term2.getStorageType());
    }
    
    protected int getStorageType(IStrategoTerm[] terms) {
    	int result = defaultStorageType;
    	for (IStrategoTerm term : terms) {
    		int type = term.getStorageType();
    		if (type < result) { 
        		if (type == 0) return 0;
    			result = type;
    		}
    	}
    	return result;
    }
    
    public IStrategoTerm copyAttachments(IStrategoTerm from, IStrategoTerm to) {
    	if (to.getStorageType() != MUTABLE)
    		throw new IllegalArgumentException("Target term is not mutable and does not support attachments");
    	ITermAttachment attach = from.getAttachment(null);
    	while (attach != null) {
    		try {
				to.putAttachment(attach.clone());
			} catch (CloneNotSupportedException e) {
				throw new IllegalArgumentException("Copying attachments of this type is not supported: " + attach.getAttachmentType(), e);
			}
    		attach = attach.getNext();
    	}
    	return to;
    }

	/**
	 * Performs a sanity check on a factory,
	 * testing if it produces terms with a storage type
	 * smaller or equal than the given value.
	 */
    public static boolean checkStorageType(ITermFactory factory, int storageType) {
		return factory.getDefaultStorageType() <= storageType
				&& factory.makeList(EMPTY).getStorageType() <= storageType
				&& factory.makeString("Sanity").getStorageType() <= storageType;
	}
}
