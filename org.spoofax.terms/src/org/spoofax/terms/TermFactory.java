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
import org.spoofax.terms.attachments.OriginAttachment;
import org.spoofax.terms.util.StringInterner;
import org.spoofax.terms.util.TermUtils;

import javax.annotation.Nullable;

import java.util.Collection;

public class TermFactory extends AbstractTermFactory implements ITermFactory {
    private static final int MAX_POOLED_STRING_LENGTH = 200;
    private static final StringInterner usedStrings = new StringInterner();

    private final IStrategoConstructor placeholderConstructor = makeConstructor("<>", 1);


    public TermFactory() {
        super();
    }

    @Override
    @Deprecated
    public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm[] terms, IStrategoList annotations) {
        assert ctr.getArity() == terms.length;
        return new StrategoAppl(ctr, terms, annotations);
    }

    @Override
    @Deprecated
    public IStrategoInt makeInt(int i) {
        return new StrategoInt(i, null);
    }

    @Override
    @Deprecated
    public IStrategoList makeList() {
        return new StrategoList(null);
    }

    @Override
    @Deprecated
    public IStrategoList makeList(IStrategoTerm[] terms, IStrategoList outerAnnos) {
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

    @Override
    @Deprecated
    public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annotations) {
        if(head == null)
            return makeList();
        return new StrategoList(head, tail, annotations);
    }

    @Override
    @Deprecated
    public IStrategoReal makeReal(double d) {
        return new StrategoReal(d, null);
    }

    @Override
    @Deprecated
    public IStrategoString makeString(String s) {
        if(s.length() <= MAX_POOLED_STRING_LENGTH) {
            synchronized(usedStrings) {
                s = usedStrings.intern(s);
            }
        }
        return new StrategoString(s, null);
    }

    @Override
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

    @Override
    @Deprecated
    public IStrategoTuple makeTuple(IStrategoTerm[] terms, IStrategoList annos) {
        return new StrategoTuple(terms, annos);
    }

    @Override
    @Deprecated
    public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
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

    @Override
    @Deprecated
    public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
        return new StrategoPlaceholder(placeholderConstructor, template, TermFactory.EMPTY_LIST);
    }




    @Override
    public IStrategoAppl buildAppl(IStrategoConstructor constructor, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        assert constructor.getArity() == subterms.length;
        StrategoAppl newTerm = new StrategoAppl(constructor, subterms, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoList buildEmptyList(@Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        StrategoList newTerm = new StrategoList(annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoList buildList(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        int size = subterms.length;
        IStrategoList.Builder builder = createListBuilder(size, replacee, annotations);
        for (IStrategoTerm subterm : subterms) {
            builder.add(subterm);
        }
        return builder.build();
    }

    @Override
    public IStrategoList buildList(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        int size = subterms instanceof Collection ? ((Collection<?>)subterms).size() : 0;
        IStrategoList.Builder builder = createListBuilder(size, replacee, annotations);
        for (IStrategoTerm subterm : subterms) {
            builder.add(subterm);
        }
        return builder.build();
    }

    @Override
    public IStrategoList buildListConsNil(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        StrategoList newTerm = new StrategoList(head, tail, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoTuple buildTuple(IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        StrategoTuple newTerm = new StrategoTuple(subterms, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoPlaceholder buildPlaceholder(IStrategoTerm template, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        StrategoPlaceholder newTerm = new StrategoPlaceholder(placeholderConstructor, template, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoInt buildInt(int value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        StrategoInt newTerm = new StrategoInt(value, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoReal buildReal(double value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        StrategoReal newTerm = new StrategoReal(value, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoString buildString(String value, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        if(value.length() <= MAX_POOLED_STRING_LENGTH) {
            synchronized(usedStrings) {
                value = usedStrings.intern(value);
            }
        }
        StrategoString newTerm = new StrategoString(value, annotations);
        replaceStrategoTerm(newTerm, replacee);
        return newTerm;
    }

    @Override
    public IStrategoTerm withAnnotations(IStrategoTerm term, @Nullable IStrategoList annotations) {
        IStrategoList currentAnnos = term.getAnnotations();

        // Cheap checks
        if (currentAnnos == annotations) {
            return term;
        } else if ((annotations == null || annotations.isEmpty()) && currentAnnos.isEmpty()) {
            return term;
        }

        // Ordered from most likely to least likely:
        if (TermUtils.isAppl(term)) {
            return buildAppl(((IStrategoAppl)term).getConstructor(), term.getAllSubterms(), term, annotations);
        } else if (TermUtils.isList(term)) {
            return buildList(term.getAllSubterms(), term, annotations);
        } else if (TermUtils.isString(term)) {
            return buildString(((IStrategoString)term).stringValue(), term, annotations);
        } else if (TermUtils.isInt(term)) {
            return buildInt(((IStrategoInt)term).intValue(), term, annotations);
        } else if (TermUtils.isTuple(term)) {
            return buildTuple(term.getAllSubterms(), term, annotations);
        } else if (TermUtils.isReal(term)) {
            return buildReal(((IStrategoReal)term).realValue(), term, annotations);
        } else if (term instanceof IStrategoPlaceholder) {
            return buildPlaceholder(((IStrategoPlaceholder)term).getTemplate(), term, annotations);
        }

        throw new UnsupportedOperationException(
                "Unable to annotate term of type " + term.getClass().getName() + " in " + getClass().getName());
    }

    @Override
    public IStrategoList.Builder createListBuilder(int initialCapacity, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        return new IStrategoList.Builder() {
            private final StrategoArrayListBuilder innerBuilder = new StrategoArrayListBuilder(initialCapacity);

            @Override
            public void add(IStrategoTerm term) {
                innerBuilder.add(term);
            }

            @Override
            public IStrategoList build() {
                IStrategoList newTerm = innerBuilder.build(annotations);
                replaceStrategoTerm(newTerm, replacee);
                return newTerm;
            }

            @Override
            public boolean isEmpty() {
                return innerBuilder.isEmpty();
            }
        };
    }

    /**
     * Replaces the replacee with the new term.
     *
     * This implementation mutates the new term in-place.
     *
     * @param newTerm the new term
     * @param replacee the term being replaced; or {@code null}
     */
    private void replaceStrategoTerm(IStrategoTerm newTerm, @Nullable IStrategoTerm replacee) {
        if (replacee == null || newTerm == replacee) return;

        preserveOrigins(newTerm, replacee);
    }

    /**
     * Copied the origin of the replaced term to the new term, unless
     * the new term already has an origin of its own.
     *
     * This implementation mutates the new term in-place.
     *
     * @param newTerm the new term
     * @param replacee the term being replaced
     */
    private void preserveOrigins(IStrategoTerm newTerm, IStrategoTerm replacee) {
        @Nullable IStrategoTerm curOrigin = OriginAttachment.getOrigin(newTerm);
        if (curOrigin != null) {
            // The term already has an origin of its own.
            return;
        }
        @Nullable IStrategoTerm newOrigin = OriginAttachment.getOrigin(replacee);
        if (newOrigin == null) {
            // The replacee doesn't have an origin,
            // so we bail out here to avoid creating a null origin attachment.
            return;
        }
        // This mutates the term in-place.
        OriginAttachment.setOrigin(newTerm, newOrigin);
    }
}
