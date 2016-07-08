package org.spoofax.terms;

import static org.spoofax.interpreter.terms.IStrategoTerm.APPL;
import static org.spoofax.interpreter.terms.IStrategoTerm.INT;
import static org.spoofax.interpreter.terms.IStrategoTerm.LIST;
import static org.spoofax.interpreter.terms.IStrategoTerm.REAL;
import static org.spoofax.interpreter.terms.IStrategoTerm.STRING;
import static org.spoofax.interpreter.terms.IStrategoTerm.TUPLE;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class Term {
    public static String stringAt(IStrategoTerm t, int i) {
        return ((IStrategoString) t.getSubterm(i)).stringValue();
    }

    public static int intAt(IStrategoTerm t, int i) {
        return ((IStrategoInt) t.getSubterm(i)).intValue();
    }

    @SuppressWarnings("unchecked") // casting is inherently unsafe, but doesn't warrant a warning here
    public static<T extends IStrategoTerm> T termAt(IStrategoTerm t, int i) {
        return (T) t.getSubterm(i);
    }
    
    public static IStrategoAppl applAt(IStrategoTerm t, int i) {
    	return (IStrategoAppl) t.getSubterm(i);
    }

    public static boolean isTermString(IStrategoTerm t) {
        return t.getTermType() == STRING;
    }

    public static String javaString(IStrategoTerm t) {
        return ((IStrategoString)t).stringValue();
    }

    public static boolean isTermList(IStrategoTerm t) {
        return t.getTermType() == LIST;
    }
    
    public static boolean isTermInt(IStrategoTerm t) {
        return t.getTermType() == INT;
    }

    public static boolean isTermReal(IStrategoTerm t) {
        return t.getTermType() == REAL;
    }

    public static boolean isTermAppl(IStrategoTerm t) {
        return t.getTermType() == APPL;
    }
    
    public static boolean isTermNamed(IStrategoTerm t) {
    	int type = t.getTermType();
    	return type == APPL || type == STRING;
    }

    public static int javaInt(IStrategoTerm term) {
        return ((IStrategoInt)term).intValue();
    }

    public static boolean hasConstructor(IStrategoAppl t, String ctorName) {
        return t.getConstructor().getName().equals(ctorName);
    }

    public static boolean isTermTuple(IStrategoTerm t) {
        return t.getTermType() == TUPLE;
    }

    public static int asJavaInt(IStrategoTerm term) {
        return ((IStrategoInt)term).intValue();
    }

    public static String asJavaString(IStrategoTerm term) {
        return ((IStrategoString)term).stringValue();
    }

    public static IStrategoConstructor tryGetConstructor(IStrategoTerm term) {
    	return term != null && term.getTermType() == APPL ? ((IStrategoAppl) term).getConstructor() : null;
    }

    public static String tryGetName(IStrategoTerm term) {
    	return term != null && term.getTermType() == APPL ? ((IStrategoAppl) term).getConstructor().getName() : null;
    }
 
    public static IStrategoTerm removeAnnotations(IStrategoTerm inTerm, final ITermFactory factory) {
        TermTransformer trans = new TermTransformer(factory, true) {
            @Override public IStrategoTerm preTransform(IStrategoTerm term) {
                switch(term.getTermType()) {
                    case IStrategoTerm.APPL:
                        return factory.makeAppl(((IStrategoAppl) term).getConstructor(), term.getAllSubterms(), null);
                    case IStrategoTerm.LIST:
                        return factory.makeList(term.getAllSubterms(), null);
                    case IStrategoTerm.STRING:
                        return factory.makeString(((IStrategoString) term).stringValue());
                    case IStrategoTerm.TUPLE:
                        return factory.makeTuple(term.getAllSubterms(), null);
                    default:
                        return term;
                }
            }
        };
        return trans.transform(inTerm);
    }
}
