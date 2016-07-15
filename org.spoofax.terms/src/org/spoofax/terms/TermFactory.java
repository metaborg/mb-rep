package org.spoofax.terms;

import static java.lang.Math.min;
import static org.spoofax.interpreter.terms.IStrategoTerm.MAXIMALLY_SHARED;
import static org.spoofax.interpreter.terms.IStrategoTerm.MUTABLE;
import static org.spoofax.interpreter.terms.IStrategoTerm.SHARABLE;
import static org.spoofax.interpreter.terms.IStrategoTerm.STRING;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

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

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 * @author Karl T. Kalleberg <karltk add strategoxt.org>
 */
public class TermFactory extends AbstractTermFactory implements ITermFactory {
    
    // Strings should be MAXIMALLY_SHARED, but we use
    // a weaker assumption instead to be safe (StrategoXT/834)
    private static final int STRING_POOL_STORAGE_TYPE = SHARABLE;
    
    private static final int INT_POOL_STORAGE_TYPE = MAXIMALLY_SHARED;
    
    public static final int MAX_POOLED_STRING_LENGTH = 100;
    
    private static final IStrategoInt[] intCache = initIntCache();
    
    private IStrategoConstructor placeholderConstructor;

    /**
     * The singleton maximally shared empty list instance.
     * 
     * Other empty lists may exists, but this is the only one
     * that may be marked maximally shared.
     */
    public static final StrategoList EMPTY_LIST =
    	new StrategoList(null, null, null, MAXIMALLY_SHARED); 
    
    // StrategoXT/801: must use weak keys and values, and must maintain maximal sharing to avoid early collection
    private final WeakHashMap<String, WeakReference<StrategoString>> asyncStringPool =
        new WeakHashMap<String, WeakReference<StrategoString>>();
    
    public TermFactory() {
    	super(SHARABLE);
    }
    
    public ITermFactory getFactoryWithStorageType(int storageType) {
    	if (storageType > SHARABLE)
    		throw new UnsupportedOperationException();
    	if (storageType == defaultStorageType)
    		return this;
    	TermFactory result = new TermFactory();
    	result.defaultStorageType = storageType;
    	return result;
    }

    @Override
	public IStrategoAppl makeAppl(IStrategoConstructor ctr,
            IStrategoTerm[] terms, IStrategoList annotations) {
    	int storageType = defaultStorageType;
		storageType = min(storageType, getStorageType(terms));
    	if (storageType != 0)
        	storageType = min(storageType, getStorageType(annotations));
    	assert ctr.getArity() == terms.length;
        return new StrategoAppl(ctr, terms, annotations, storageType);
    }

    public IStrategoInt makeInt(int i) {
    	if (0 <= i && i <= 255 && isTermSharingAllowed())
    		return intCache[i];
    	return new StrategoInt(i, null, defaultStorageType);
    }

    private static final IStrategoInt[] initIntCache() {
    	IStrategoInt[] results = new IStrategoInt[256];
    	for (int i = 0; i < results.length; i++) {
    		results[i] = new StrategoInt(i, INT_POOL_STORAGE_TYPE);
    	}
    	return results;
    }
    
    @Override
	public IStrategoList makeList() {
    	return isTermSharingAllowed() ? EMPTY_LIST : new StrategoList(null, null, null, defaultStorageType);
    }
    
    @Override
	public IStrategoList makeList(IStrategoTerm[] terms, IStrategoList outerAnnos) {
    	int storageType = defaultStorageType;
        IStrategoList result = makeList();
        int i = terms.length - 1;
        while (i > 0) {
        	IStrategoTerm head = terms[i--];
            storageType = min(storageType, getStorageType(head));
			result = new StrategoList(head, result, null, storageType);
        }
        if (i == 0) {
        	IStrategoTerm head = terms[0];
            storageType = min(storageType, getStorageType(head));
			result = new StrategoList(head, result, outerAnnos, storageType);
        } else {
        	if (outerAnnos == null || outerAnnos.isEmpty()) {
        		return makeList();
        	} else {
        		return new StrategoList(null, null, outerAnnos, defaultStorageType);
        	}
        }
        return result;
    }
    
    @Override
	public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annotations) {
    	int storageType = min(defaultStorageType, getStorageType(head, tail));
    	
    	if (head == null) return makeList();
    	return new StrategoList(head, tail, annotations, storageType);
    }

    public IStrategoReal makeReal(double d) {
        return new StrategoReal(d, null, defaultStorageType);
    }

    public IStrategoString makeString(String s) {
    	if (s.length() > MAX_POOLED_STRING_LENGTH)
    		return new StrategoString(s, null, defaultStorageType);

    	synchronized (asyncStringPool) {
	    	WeakReference<StrategoString> resultRef = asyncStringPool.get(s);
	    	StrategoString result = resultRef == null ? null : resultRef.get();
	    	int type = isTermSharingAllowed() ? STRING_POOL_STORAGE_TYPE : MUTABLE;
	    	if (result == null) {
    			// StrategoString was garbage collected, but key string might still be in memory
    			// Reallocation should be safe either way
				result = new StrategoString(s, null, type);
    			asyncStringPool.put(s, new WeakReference<StrategoString>(result));
	    	} else if (result.getStorageType() == MUTABLE || !isTermSharingAllowed()) {
	    		// We cannot reuse the existing string,
	    		// but to satisfy the tryMakeUniqueString() contract
	    		// we *must* keep a reference to the original
	    		// hashmap key alive as long as our result lives
	    		//
	    		// UNDONE: actually, the hashtable key is s 
	    		//   result = new KeepAliveString(s, result);
	    		result = new StrategoString(s, null, type);
        	}
	    	return result;
    	}
    }
    
    public IStrategoString tryMakeUniqueString(String name) {
        synchronized (asyncStringPool) {
        	if (asyncStringPool.containsKey(name)) {
        		return null;
        	} else if (name.length() > MAX_POOLED_STRING_LENGTH) {
        		throw new UnsupportedOperationException("String too long to be pooled (newname not allowed): " + name);
        	} else {
            	StrategoString result = new StrategoString(name, null,
            			isTermSharingAllowed() ? STRING_POOL_STORAGE_TYPE : MUTABLE);
        		asyncStringPool.put(name, new WeakReference<StrategoString>(result));
        		return result;
        	}
        }
    }

    @Override
	public IStrategoTuple makeTuple(IStrategoTerm[] terms, IStrategoList annos) {
        int storageType = min(defaultStorageType, getStorageType(terms));
		return new StrategoTuple(terms, annos, storageType);
    }
    
    public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
        IStrategoList currentAnnos = term.getAnnotations();
		if (currentAnnos == annotations) { // cheap check
            return term;
        } else if (term.getStorageType() == MAXIMALLY_SHARED) {
			if (term == EMPTY_LIST) {
				if (annotations == EMPTY_LIST || annotations.isEmpty()) {
					return EMPTY_LIST;
				} else {
					return new StrategoList(null, null, annotations, defaultStorageType);
				}
			} else if (term.getTermType() == STRING) {
				String value = ((IStrategoString) term).stringValue();
				if (annotations == EMPTY_LIST || annotations.isEmpty()) {
					return makeString(value);
				} else {
					return new StrategoString(value, annotations, defaultStorageType);
				}
			} else if (currentAnnos == EMPTY_LIST) {
				return annotations.isEmpty() ? term : new StrategoAnnotation(this, term, annotations);
			} else if (term instanceof StrategoAnnotation) {
				term = ((StrategoAnnotation) term).getWrapped();
				// int storageType = min(defaultStorageType, getStorageType(term));
				return new StrategoAnnotation(this, term, annotations);
			} else {
				throw new UnsupportedOperationException("Unable to annotate term of type " + term.getClass().getName());
			}
        } else if ((annotations == EMPTY_LIST || annotations.isEmpty()) && term.getTermType() == STRING) {
    		return makeString(((IStrategoString) term).stringValue());
        } else if (term instanceof StrategoTerm) {
        	StrategoTerm result = ((StrategoTerm) term).clone(true);
    	    result.internalSetAnnotations(annotations);
    	    assert result.getStorageType() != MAXIMALLY_SHARED;
    	    return result;
    	} else {
            throw new UnsupportedOperationException("Unable to annotate term of type " + term.getClass().getName() + " in " + getClass().getName());
        }
    }

	public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
        if (placeholderConstructor == null)
            placeholderConstructor = makeConstructor("<>", 1);
        return new StrategoPlaceholder(placeholderConstructor, template, TermFactory.EMPTY_LIST, defaultStorageType);
	}

    /*
     * A Stratego string that maintains 
     * a reference to an existing string,
     * that must be kept alive by the garbage collector
     * at least as long as the current string is alive. 
     * 
     * @author Lennart Kats <lennart add lclnet.nl>
     *
    static class KeepAliveString extends StrategoString {
		final Object gcKeepAlive;

		public KeepAliveString(String s, Object gcKeepAlive) {
			super(s, null, MUTABLE);
			this.gcKeepAlive = gcKeepAlive;
		}
	}
	 */

}
