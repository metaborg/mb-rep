package org.spoofax.interpreter.adapter.aterm;

import java.util.WeakHashMap;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermFactory;
import aterm.ATermInt;
import aterm.ATermList;
import aterm.ATermReal;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class ATermConverter {
    
    private static final int INT_CACHE_MAX = 255;

    private final ATermFactory atermFactory;

    private final ITermFactory factory;

    private final boolean useSharing;

    private final WeakHashMap<ATerm, IStrategoTerm> cache = new WeakHashMap<ATerm, IStrategoTerm>();
    
    private final ATermList emptyList;
    
    private final IStrategoInt[] intCache = new IStrategoInt[INT_CACHE_MAX + 1];

    public ATermConverter(ATermFactory atermFactory, ITermFactory factory,
            boolean useSharing) {
        this.atermFactory = atermFactory;
        this.factory = factory;
        this.useSharing = useSharing;
        emptyList = atermFactory == null ? null : atermFactory.makeList();
    }

    // ATerms to StrategoTerms

    public IStrategoTerm[] convertAll(ATerm[] terms) {
        IStrategoTerm[] results = new IStrategoTerm[terms.length];
        for (int i = 0; i < terms.length; i++) {
            results[i] = convert(terms[i]);
        }
        return results;
    }

    public IStrategoTerm convert(ATerm term) {
        if (factory == null)
            throw new IllegalStateException("Term factory is null");
        
        if (factory instanceof WrappedATermFactory)
            return ((WrappedATermFactory) factory).wrapTerm(term);
        
        int type = term.getType();

        if (type == ATerm.INT) return convert((ATermInt) term);
        
        IStrategoTerm result;
        if (useSharing) {
            result = cache.get(term);
            if (result != null) return result;
        }
        
        switch (type) {
            // APPL and LIST are inlined to help stack usage
            case ATerm.APPL:
                ATermAppl appl = (ATermAppl) term;
                if (appl.isQuoted() && appl.getArity() == 0) {
                    return convertString(appl);
                } else if (appl.getName().length() == 0) {
                    return convertTuple(appl);
                } else {
                    ATerm[] aterms = appl.getArgumentArray();
                    IStrategoTerm[] terms = new IStrategoTerm[aterms.length];
                    for (int i = 0; i < aterms.length; i++) {
                        terms[i] = convert(aterms[i]);
                    }
                    IStrategoConstructor ctor = convert(appl.getAFun());
                    result = annotate(factory.makeAppl(ctor, terms), appl);
                    break;
                }
            case ATerm.LIST:
                ATermList list = (ATermList) term;
                IStrategoTerm[] terms = new IStrategoTerm[list.getLength()];
                for (int i = 0; i < terms.length; i++) {
                    terms[i] = convert(list.getFirst());
                    list = list.getNext();
                }
                result = annotate(factory.makeList(terms), list);
                break;
            case ATerm.AFUN:
                result = convert((AFun) term);
                break;
            case ATerm.REAL:
                result = convert((ATermReal) term);
                break;
            case ATerm.BLOB:
                throw new UnsupportedOperationException("Converting an ATerm blob to a Stratego term");
            default:
                throw new IllegalStateException("Unknown term type: "
                        + term.getClass().getSimpleName());
        }

        if (useSharing) cache.put(term, result);
        return result;
    }

    private IStrategoConstructor convert(AFun constructor) {
        return factory.makeConstructor(constructor.getName(), constructor.getArity());
    }

    private IStrategoInt convert(ATermInt term) {
        IStrategoInt result;
        int value = term.getInt();
        
        if (0 <= value && value <= INT_CACHE_MAX) {
            result = intCache[value];
            if (result == null) result = intCache[value] = factory.makeInt(value);
            return annotate(result, term);
        }
        
        result = (IStrategoInt) cache.get(term);
        if (result != null) return result;
        
        result = annotate(factory.makeInt(value), term);
        cache.put(term, result);
        return result;
    }

    private IStrategoReal convert(ATermReal term) {
        return annotate(factory.makeReal(term.getReal()), term);
    }

    private IStrategoString convertString(ATermAppl term) {
        return annotate(factory.makeString(term.getName()), term);
    }

    private IStrategoTuple convertTuple(ATermAppl term) {
        IStrategoTerm[] subterms = convertAll(term.getArgumentArray());
        return annotate(factory.makeTuple(subterms), term);
    }

    @SuppressWarnings("unchecked")
    protected <T extends IStrategoTerm> T annotate(T term, ATerm input) {
        ATermList annotations = input.getAnnotations();
        if (annotations == null || annotations == emptyList) {
            return term;
        } else {
            return (T) factory.annotateTerm(term, (IStrategoList) convert(annotations));
        }
    }

    // StrategoTerms to ATerms

    public ATerm[] convertAll(IStrategoTerm[] terms) {
        ATerm[] results = new ATerm[terms.length];
        for (int i = 0; i < terms.length; i++) {
            results[i] = convert(terms[i]);
        }
        return results;
    }

    public ATerm convert(IStrategoTerm term) {
        if (atermFactory == null)
            throw new IllegalStateException("ATerm factory is null");
        
        if (term instanceof WrappedATerm && ((WrappedATerm) term).parent == atermFactory)
            return ((WrappedATerm) term).getATerm();

        switch (term.getTermType()) {
            // APPL and LIST are inlined to help stack usage
            case IStrategoTerm.APPL:
                IStrategoAppl appl = (IStrategoAppl) term;
                IStrategoTerm[] terms = appl.getAllSubterms();
                ATerm[] aterms = new ATerm[terms.length];
                for (int i = 0; i < terms.length; i++) {
                    aterms[i] = convert(terms[i]);
                }
                AFun ctor = convert(appl.getConstructor());
                return annotate(atermFactory.makeAppl(ctor, aterms), appl);
            case IStrategoTerm.LIST:
                IStrategoList list = (IStrategoList) term;
                IStrategoTerm[] terms2 = list.getAllSubterms();
                ATermList result = atermFactory.makeList();
                for (int i = terms2.length - 1; i >= 0; i--) {
                    result = atermFactory.makeList(convert(terms2[i]), result);
                }
                return result;
            case IStrategoTerm.CTOR:
                return convert((IStrategoConstructor) term);
            case IStrategoTerm.INT:
                return convert((IStrategoInt) term);
            case IStrategoTerm.REAL:
                return convert((IStrategoReal) term);
            case IStrategoTerm.STRING:
                return convert((IStrategoString) term);
            case IStrategoTerm.TUPLE:
                return convert((IStrategoTuple) term);
            case IStrategoTerm.BLOB:
                throw new UnsupportedOperationException("Converting an blob to an ATerm");
            default:
                throw new IllegalStateException("Unknown term type: "
                        + term.getClass().getSimpleName());
        }
    }

    public AFun convert(IStrategoConstructor constructor) {
        return atermFactory.makeAFun(constructor.getName(), constructor.getArity(), false);
    }

    private ATermInt convert(IStrategoInt term) {
        return annotate(atermFactory.makeInt(term.intValue()), term);
    }

    private ATermReal convert(IStrategoReal term) {
        return annotate(atermFactory.makeReal(term.realValue()), term);
    }

    private ATermAppl convert(IStrategoString term) {
        AFun fun = atermFactory.makeAFun(term.stringValue(), 0, true);
        return annotate(atermFactory.makeAppl(fun), term);
    }

    private ATermAppl convert(IStrategoTuple term) {
        AFun fun = atermFactory.makeAFun("", term.getSubtermCount(), false);
        ATerm[] subterms = convertAll(term.getAllSubterms());
        return annotate(atermFactory.makeAppl(fun, subterms), term);
    }

    @SuppressWarnings("unchecked")
    protected <T extends ATerm> T annotate(T term, IStrategoTerm input) {
        IStrategoList annotations = input.getAnnotations();
        if (annotations == null || annotations.isEmpty()) {
            return term;
        } else {
            return (T) term.setAnnotations((ATermList) convert(annotations));
        }
    }
}
