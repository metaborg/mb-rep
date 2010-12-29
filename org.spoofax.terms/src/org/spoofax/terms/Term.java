package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

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

    public static IStrategoReal realAt(IStrategoList t, int i) {
        return (IStrategoReal) t.getSubterm(i);
    }

    public static boolean isTermString(IStrategoTerm t) {
        return t.getTermType() == IStrategoTerm.STRING;
    }

    public static String javaString(IStrategoTerm t) {
        return ((IStrategoString)t).stringValue();
    }

    public static boolean isTermList(IStrategoTerm t) {
        return t.getTermType() == IStrategoTerm.LIST;
    }
    
    public static boolean isTermInt(IStrategoTerm t) {
        return t.getTermType() == IStrategoTerm.INT;
    }

    public static boolean isTermReal(IStrategoTerm t) {
        return t.getTermType() == IStrategoTerm.REAL;
    }

    public static boolean isTermAppl(IStrategoTerm t) {
        return t.getTermType() == IStrategoTerm.APPL;
    }
    
    public static boolean isTermNamed(IStrategoTerm t) {
    	int type = t.getTermType();
    	return type == IStrategoTerm.APPL || type == IStrategoTerm.STRING;
    }

    public static int javaInt(IStrategoTerm term) {
        return ((IStrategoInt)term).intValue();
    }

    public static boolean hasConstructor(IStrategoAppl t, String ctorName) {
        return t.getConstructor().getName().equals(ctorName);
    }

    public static boolean isTermTuple(IStrategoTerm t) {
        return t.getTermType() == IStrategoTerm.TUPLE;
    }

    public static int asJavaInt(IStrategoTerm term) {
        return ((IStrategoInt)term).intValue();
    }

    public static String asJavaString(IStrategoTerm term) {
        return ((IStrategoString)term).stringValue();
    }


}
