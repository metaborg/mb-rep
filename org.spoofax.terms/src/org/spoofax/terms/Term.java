package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.util.TermUtils;

/** @deprecated Use {@link TermUtils} */
@Deprecated
public class Term {

    /** @deprecated Use {@link TermUtils#toJavaStringAt} */
    @Deprecated
    public static String stringAt(IStrategoTerm t, int i) {
        return TermUtils.toJavaStringAt(t, i);
    }

    /** @deprecated Use {@link TermUtils#toJavaIntAt} */
    @Deprecated
    public static int intAt(IStrategoTerm t, int i) {
        return TermUtils.toJavaIntAt(t, i);
    }

    /** @deprecated Use {@link IStrategoTerm#getSubterm} */
    @Deprecated
    @SuppressWarnings("unchecked") // casting is inherently unsafe, but doesn't warrant a warning here
    public static<T extends IStrategoTerm> T termAt(IStrategoTerm t, int i) {
        return (T) t.getSubterm(i);
    }

    /** @deprecated Use {@link TermUtils#toApplAt} */
    @Deprecated
    public static IStrategoAppl applAt(IStrategoTerm t, int i) {
        return TermUtils.toApplAt(t, i);
    }

    /** @deprecated Use {@link TermUtils#isString} */
    @Deprecated
    public static boolean isTermString(IStrategoTerm t) {
        return TermUtils.isString(t);
    }

    /** @deprecated Use {@link TermUtils#toJavaString} */
    @Deprecated
    public static String javaString(IStrategoTerm t) {
        return TermUtils.toJavaString(t);
    }

    /** @deprecated Use {@link TermUtils#isList} */
    @Deprecated
    public static boolean isTermList(IStrategoTerm t) {
        return TermUtils.isList(t);
    }

    /** @deprecated Use {@link TermUtils#isInt} */
    @Deprecated
    public static boolean isTermInt(IStrategoTerm t) {
        return TermUtils.isInt(t);
    }

    /** @deprecated Use {@link TermUtils#isReal} */
    @Deprecated
    public static boolean isTermReal(IStrategoTerm t) {
        return TermUtils.isReal(t);
    }

    /** @deprecated Use {@link TermUtils#isAppl} */
    @Deprecated
    public static boolean isTermAppl(IStrategoTerm t) {
        return TermUtils.isAppl(t);
    }

    /** @deprecated */
    @Deprecated
    public static boolean isTermNamed(IStrategoTerm t) {
        return TermUtils.isAppl(t) || TermUtils.isString(t);
    }

    /** @deprecated Use {@link TermUtils#toJavaInt} */
    @Deprecated
    public static int javaInt(IStrategoTerm term) {
        return TermUtils.toJavaInt(term);
    }

    /** @deprecated Use {@link TermUtils#isAppl} */
    @Deprecated
    public static boolean hasConstructor(IStrategoAppl t, String ctorName) {
        return TermUtils.isAppl(t, ctorName);
    }

    /** @deprecated Use {@link TermUtils#isTuple} */
    @Deprecated
    public static boolean isTermTuple(IStrategoTerm t) {
        return TermUtils.isTuple(t);
    }

    /** @deprecated Use {@link TermUtils#toJavaInt} */
    @Deprecated
    public static int asJavaInt(IStrategoTerm term) {
        return TermUtils.toJavaInt(term);
    }

    /** @deprecated Use {@link TermUtils#toJavaString} */
    @Deprecated
    public static String asJavaString(IStrategoTerm term) {
        return TermUtils.toJavaString(term);
    }

    /** @deprecated Use {@link TermUtils#asAppl} with {@link IStrategoAppl#getConstructor()}. */
    @Deprecated
    public static IStrategoConstructor tryGetConstructor(IStrategoTerm term) {
        return TermUtils.asAppl(term).map(IStrategoAppl::getConstructor).orElse(null);
    }

    /** @deprecated Use {@link TermUtils#asAppl} with {@link IStrategoAppl#getConstructor()}. */
    @Deprecated
    public static String tryGetName(IStrategoTerm term) {
        return TermUtils.asAppl(term).map(a -> a.getConstructor().getName()).orElse(null);
    }

    @Deprecated
    public static IStrategoTerm removeAnnotations(IStrategoTerm inTerm, final ITermFactory factory) {
        TermTransformer trans = new TermTransformer(factory, true) {
            @Override public IStrategoTerm preTransform(IStrategoTerm term) {
                switch(term.getType()) {
                    case APPL:
                        return factory.makeAppl(((IStrategoAppl) term).getConstructor(), term.getAllSubterms(), null);
                    case LIST:
                        return factory.makeList(term.getAllSubterms(), null);
                    case STRING:
                        return factory.makeString(((IStrategoString) term).stringValue());
                    case TUPLE:
                        return factory.makeTuple(term.getAllSubterms(), null);
                    default:
                        return term;
                }
            }
        };
        return trans.transform(inTerm);
    }
}
