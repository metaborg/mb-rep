package org.spoofax.terms;

import static java.lang.Math.min;
import static org.spoofax.interpreter.terms.IStrategoTerm.*;

import java.util.Collections;
import java.util.Set;
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

public class TermFactory extends AbstractTermFactory implements ITermFactory {
    private static final int MAX_POOLED_STRING_LENGTH = 100;
    private static final Set<String> usedStrings = Collections.newSetFromMap(new WeakHashMap<String, Boolean>());

    private IStrategoConstructor placeholderConstructor;


    public TermFactory() {
        super(SHARABLE);
    }


    public ITermFactory getFactoryWithStorageType(int storageType) {
        if(storageType > SHARABLE)
            throw new UnsupportedOperationException();
        if(storageType == defaultStorageType)
            return this;
        TermFactory result = new TermFactory();
        result.defaultStorageType = storageType;
        return result;
    }

    @Override public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm[] terms,
        IStrategoList annotations) {
        int storageType = defaultStorageType;
        storageType = min(storageType, getStorageType(terms));
        if(storageType != 0)
            storageType = min(storageType, getStorageType(annotations));
        assert ctr.getArity() == terms.length;
        return new StrategoAppl(ctr, terms, annotations, storageType);
    }

    public IStrategoInt makeInt(int i) {
        return new StrategoInt(i, null, defaultStorageType);
    }

    @Override public IStrategoList makeList() {
        return isTermSharingAllowed() ? EMPTY_LIST : new StrategoList(null, null, null, defaultStorageType);
    }

    @Override public IStrategoList makeList(IStrategoTerm[] terms, IStrategoList outerAnnos) {
        int storageType = defaultStorageType;
        IStrategoList result = makeList();
        int i = terms.length - 1;
        while(i > 0) {
            IStrategoTerm head = terms[i--];
            storageType = min(storageType, getStorageType(head));
            result = new StrategoList(head, result, null, storageType);
        }
        if(i == 0) {
            IStrategoTerm head = terms[0];
            storageType = min(storageType, getStorageType(head));
            result = new StrategoList(head, result, outerAnnos, storageType);
        } else {
            if(outerAnnos == null || outerAnnos.isEmpty()) {
                return makeList();
            } else {
                return new StrategoList(null, null, outerAnnos, defaultStorageType);
            }
        }
        return result;
    }

    @Override public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annotations) {
        int storageType = min(defaultStorageType, getStorageType(head, tail));

        if(head == null)
            return makeList();
        return new StrategoList(head, tail, annotations, storageType);
    }

    public IStrategoReal makeReal(double d) {
        return new StrategoReal(d, null, defaultStorageType);
    }

    public IStrategoString makeString(String s) {
        final IStrategoString string = new StrategoString(s, null, defaultStorageType);
        if(s.length() <= MAX_POOLED_STRING_LENGTH) {
            synchronized(usedStrings) {
                usedStrings.add(s);
            }
        }
        return string;
    }

    public IStrategoString tryMakeUniqueString(String s) {
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

    @Override public IStrategoTuple makeTuple(IStrategoTerm[] terms, IStrategoList annos) {
        int storageType = min(defaultStorageType, getStorageType(terms));
        return new StrategoTuple(terms, annos, storageType);
    }

    public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
        IStrategoList currentAnnos = term.getAnnotations();
        if(currentAnnos == annotations) { // cheap check
            return term;
        } else if(term.getStorageType() == MAXIMALLY_SHARED) {
            if(term == EMPTY_LIST) {
                if(annotations == EMPTY_LIST || annotations.isEmpty()) {
                    return EMPTY_LIST;
                } else {
                    return new StrategoList(null, null, annotations, defaultStorageType);
                }
            } else if(term.getTermType() == STRING) {
                String value = ((IStrategoString) term).stringValue();
                if(annotations == EMPTY_LIST || annotations.isEmpty()) {
                    return makeString(value);
                } else {
                    return new StrategoString(value, annotations, defaultStorageType);
                }
            } else if(currentAnnos == EMPTY_LIST) {
                return annotations.isEmpty() ? term : new StrategoAnnotation(this, term, annotations);
            } else if(term instanceof StrategoAnnotation) {
                term = ((StrategoAnnotation) term).getWrapped();
                // int storageType = min(defaultStorageType, getStorageType(term));
                return new StrategoAnnotation(this, term, annotations);
            } else {
                throw new UnsupportedOperationException("Unable to annotate term of type " + term.getClass().getName());
            }
        } else if((annotations == EMPTY_LIST || annotations.isEmpty()) && term.getTermType() == STRING) {
            return makeString(((IStrategoString) term).stringValue());
        } else if(term instanceof StrategoTerm) {
            StrategoTerm result = ((StrategoTerm) term).clone(true);
            result.internalSetAnnotations(annotations);
            assert result.getStorageType() != MAXIMALLY_SHARED;
            return result;
        } else {
            throw new UnsupportedOperationException(
                "Unable to annotate term of type " + term.getClass().getName() + " in " + getClass().getName());
        }
    }

    public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
        if(placeholderConstructor == null)
            placeholderConstructor = makeConstructor("<>", 1);
        return new StrategoPlaceholder(placeholderConstructor, template, TermFactory.EMPTY_LIST, defaultStorageType);
    }
}
