package org.spoofax.interpreter.terms;

import static org.spoofax.interpreter.terms.IStrategoTerm.APPL;
import static org.spoofax.interpreter.terms.IStrategoTerm.BLOB;
import static org.spoofax.interpreter.terms.IStrategoTerm.CTOR;
import static org.spoofax.interpreter.terms.IStrategoTerm.INT;
import static org.spoofax.interpreter.terms.IStrategoTerm.LIST;
import static org.spoofax.interpreter.terms.IStrategoTerm.REAL;
import static org.spoofax.interpreter.terms.IStrategoTerm.STRING;
import static org.spoofax.interpreter.terms.IStrategoTerm.TUPLE;

import java.util.HashMap;

/**
 * Copies terms created by one {@link ITermFactory} to
 * terms created by another (or the same) factory. 
 * 
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class TermConverter {
    
    private final ITermFactory factory;
    
    private final HashMap<IStrategoConstructor, IStrategoConstructor> constructors =
    	new HashMap<IStrategoConstructor, IStrategoConstructor>();
    
    private boolean isOriginEnabled;
    
    public TermConverter(ITermFactory factory) {
        this.factory = factory;
    }
    
    /**
     * Sets whether to use origin tracking when converting terms.
     */
    public void setOriginEnabled(boolean isOriginEnabled) {
		this.isOriginEnabled = isOriginEnabled;
	}
    
    public IStrategoTerm[] convertAll(IStrategoTerm[] terms) {
        IStrategoTerm[] results = new IStrategoTerm[terms.length];
        for (int i = 0; i < terms.length; i++) {
            results[i] = convert(terms[i]);
        }
        return results;
    }
    
    public static IStrategoTerm convert(ITermFactory factory, IStrategoTerm term) {
        return new TermConverter(factory).convert(term);
    }
    
    public IStrategoTerm convert(IStrategoTerm term) {
    	IStrategoTerm result;
        switch (term.getTermType()) {
            // APPL and LIST are inlined to help stack usage
            case APPL:
                IStrategoAppl appl = (IStrategoAppl) term;
                IStrategoTerm[] terms = appl.getAllSubterms();
                IStrategoTerm[] subTerms = new IStrategoTerm[terms.length];
                for (int i = 0; i < terms.length; i++) {
                    subTerms[i] = convert(terms[i]);
                }
                IStrategoConstructor ctor = convert(appl.getConstructor());
                result = annotate(factory.makeAppl(ctor, subTerms), appl); break;
            case LIST:
                IStrategoList list = (IStrategoList) term;
                IStrategoTerm[] terms2 = list.getAllSubterms();
                IStrategoTerm[] subTerms2 = new IStrategoTerm[terms2.length];
                for (int i = 0; i < terms2.length; i++) {
                    subTerms2[i] = convert(terms2[i]);
                }
                result =  annotate(factory.makeList(subTerms2), list); break;
            case CTOR: result =  convert((IStrategoConstructor) term); break;
            case INT: result =  convert((IStrategoInt) term); break;
            case REAL: result =  convert((IStrategoReal) term); break;
            case STRING: result =  convert((IStrategoString) term); break;
            case TUPLE: result =  convert((IStrategoTuple) term); break;
            case BLOB: result =  term; break;
            default:
                throw new IllegalStateException("Unknown term type: " + term.getClass().getName());
        }
        if (isOriginEnabled)
        	factory.replaceTerm(result, term);
        return result;
    }

    public final IStrategoAppl convert(IStrategoAppl term) {
        IStrategoTerm[] subTerms = convertAll(term.getAllSubterms());
        IStrategoConstructor ctor = convert(term.getConstructor());
        return annotate(factory.makeAppl(ctor, subTerms), term);
    }

    public IStrategoConstructor convert(IStrategoConstructor constructor) {
    	IStrategoConstructor cached = constructors.get(constructor);
    	if (cached == null) {
    	    cached = factory.makeConstructor(constructor.getName(), constructor.getArity());
    	    constructors.put(constructor, cached);
    	}
    	return cached;
    }

    public IStrategoInt convert(IStrategoInt term) {
    	if (term.isUniqueValueTerm()) {
    		// re-creating this one destroys term equality and creates subtle bugs..
    		throw new IllegalArgumentException("Cannot recreate a tree that contains <newterm> terms:" + term);
    	} else {
    		return annotate(factory.makeInt(term.intValue()), term);
    	}
    }

    public final IStrategoList convert(IStrategoList term) {
        IStrategoTerm[] subterms = convertAll(term.getAllSubterms());
        return annotate(factory.makeList(subterms), term);
    }

    public IStrategoReal convert(IStrategoReal term) {
        return annotate(factory.makeReal(term.realValue()), term);
    }

    public IStrategoString convert(IStrategoString term) {
        return annotate(factory.makeString(term.stringValue()), term);
    }

    public IStrategoTuple convert(IStrategoTuple term) {
        IStrategoTerm[] subterms = convertAll(term.getAllSubterms());
        return annotate(factory.makeTuple(subterms), term);
    }
    
    @SuppressWarnings("unchecked")
    protected<T extends IStrategoTerm> T annotate(T term, T input) {
        IStrategoList annotations = input.getAnnotations();
        if (annotations == null || annotations.isEmpty()) {
            return term;
        } else {
            return (T) factory.annotateTerm(term, convert(annotations));
        }
    }

}
