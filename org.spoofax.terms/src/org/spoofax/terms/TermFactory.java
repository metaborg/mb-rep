package org.spoofax.terms;

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
import org.spoofax.terms.util.StringInterner;
import org.spoofax.terms.util.TermUtils;

import jakarta.annotation.Nullable;

public class TermFactory extends AbstractTermFactory implements ITermFactory {
    private static final int MAX_POOLED_STRING_LENGTH = 200;
    private static final StringInterner usedStrings = new StringInterner();

    private IStrategoConstructor placeholderConstructor;


    public TermFactory() {
        super();
    }

    @Override public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm[] terms, @Nullable IStrategoList annotations) {
        assert ctr.getArity() == terms.length;
        return new StrategoAppl(ctr, terms, annotations);
    }

    @Override public IStrategoInt makeInt(int i) {
        return new StrategoInt(i, null);
    }

    @Override public IStrategoList makeList() {
        return new StrategoList(null);
    }

    @Override public IStrategoList makeList(IStrategoTerm[] terms, @Nullable IStrategoList outerAnnos) {
        IStrategoList result = makeList();
        int i = terms.length - 1;
        while(i > 0) {
            IStrategoTerm head = terms[i--];
            result = new StrategoList(head, result, null);
        }
        if(i == 0) {
            IStrategoTerm head = terms[0];
            result = new StrategoList(head, result, outerAnnos);
        } else {
            if(outerAnnos == null || outerAnnos.isEmpty()) {
                return makeList();
            } else {
                return new StrategoList(outerAnnos);
            }
        }
        return result;
    }

    @Override public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoList annotations) {
        if(head == null)
            return makeList();
        return new StrategoList(head, tail, annotations);
    }

    @Override public IStrategoReal makeReal(double d) {
        return new StrategoReal(d, null);
    }

    @Override public IStrategoString makeString(String s) {
        if(s.length() <= MAX_POOLED_STRING_LENGTH && endsWithDigit(s)) {
            synchronized(usedStrings) {
                s = usedStrings.intern(s);
            }
        }
        return new StrategoString(s, null);
    }

    public static final boolean endsWithDigit(String s) {
        if(s.isEmpty()) {
            return false;
        }
        char lastChar = s.charAt(s.length() - 1);
        return '0' <= lastChar && lastChar <= '9';
    }

    @Override public IStrategoString tryMakeUniqueString(String s) {
        synchronized(usedStrings) {
            if(usedStrings.contains(s)) {
                return null;
            } else if(s.length() > MAX_POOLED_STRING_LENGTH) {
                throw new UnsupportedOperationException("String too long to be pooled (newname not allowed): " + s);
            } else {
                return makeString(s);
            }
        }
    }

    @Override public IStrategoTuple makeTuple(IStrategoTerm[] terms, @Nullable IStrategoList annos) {
        return new StrategoTuple(terms, annos);
    }

    @Override public IStrategoTerm annotateTerm(IStrategoTerm term, @Nullable IStrategoList annotations) {
        IStrategoList currentAnnos = term.getAnnotations();
        if(currentAnnos == annotations) { // cheap check
            return term;
        } else if(annotations.isEmpty() && TermUtils.isString(term)) {
            return makeString(((IStrategoString) term).stringValue());
        } else if(term instanceof StrategoTerm) {
            StrategoTerm result = ((StrategoTerm) term).clone(true);
            result.internalSetAnnotations(annotations);
            return result;
        } else {
            throw new UnsupportedOperationException(
                "Unable to annotate term of type " + term.getClass().getName() + " in " + getClass().getName());
        }
    }

    @Override public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
        if(placeholderConstructor == null)
            placeholderConstructor = makeConstructor("<>", 1);
        return new StrategoPlaceholder(placeholderConstructor, template, TermFactory.EMPTY_LIST);
    }
}
