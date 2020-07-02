package org.spoofax.terms;

import java.util.Collection;
import java.util.HashMap;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.io.TAFTermReader;
import org.spoofax.terms.util.IterableUtils;

import javax.annotation.Nullable;

public abstract class AbstractTermFactory implements ITermFactory {
    /** An empty Stratego list. Use this instead of `new StrategoList(null)` to avoid allocating a new object. */
    public static final IStrategoList EMPTY_LIST = new StrategoList(null);

    /** An empty array of terms. Use this instead of `new IStrategoTerm[0]` to avoid allocating a new array. */
    public static final IStrategoTerm[] EMPTY_TERM_ARRAY = new IStrategoTerm[0];
    /** @deprecated Use {@link #EMPTY_TERM_ARRAY} */
    @Deprecated public static final IStrategoTerm[] EMPTY = EMPTY_TERM_ARRAY;


    private static final HashMap<StrategoConstructor, StrategoConstructor> asyncCtorCache = new HashMap<>();
    private final TAFTermReader reader = new TAFTermReader(this);

    static StrategoConstructor createCachedConstructor(String name, int arity) {
        StrategoConstructor result = new StrategoConstructor(name, arity);
        synchronized(TermFactory.class) {
            StrategoConstructor cached = asyncCtorCache.get(result);
            if(cached == null) {
                asyncCtorCache.put(result, result);
            } else {
                result = cached;
            }
        }
        return result;
    }

    @Override public StrategoConstructor makeConstructor(String name, int arity) {
        return createCachedConstructor(name, arity);
    }

    @Override
    @Deprecated
    public abstract IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
        IStrategoList annotations);

    @Override
    @Deprecated
    public abstract IStrategoTuple makeTuple(IStrategoTerm[] kids, IStrategoList annotations);

    @Override
    @Deprecated
    public abstract IStrategoList makeList(IStrategoTerm[] kids, IStrategoList annotations);

    @Override
    @Deprecated
    public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old) {
        return makeAppl(constructor, kids, old.getAnnotations());
    }

    @Override
    @Deprecated
    public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
        return makeTuple(kids, old.getAnnotations());
    }

    @Override
    @Deprecated
    public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old) {
        return makeList(kids, old.getAnnotations());
    }

    @Override
    @Deprecated
    public IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail, IStrategoTerm oldHead,
        IStrategoList oldTail) {
        return makeListCons(head, tail);
    }

    @Override
    @Deprecated
    public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
        return term;
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids, @Nullable IStrategoList annotations) {
        return makeAppl(ctr, kids.getAllSubterms(), annotations);
    }

    @Deprecated
    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids) {
        return makeAppl(ctr, kids, null);
    }

    @Override
    @Deprecated
    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... terms) {
        return makeAppl(ctr, terms, null);
    }

    @Override
    @Deprecated
    public final IStrategoList makeList(IStrategoTerm... terms) {
        return makeList(terms, null);
    }

    @Override
    @Deprecated
    public IStrategoList makeList(Collection<? extends IStrategoTerm> terms) {
        return StrategoArrayList.fromCollection(terms);
    }

    @Override
    @Deprecated
    public final IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail) {
        return makeListCons(head, tail, null);
    }

    @Override
    @Deprecated
    public abstract IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annos);

    @Override
    @Deprecated
    public final IStrategoTuple makeTuple(IStrategoTerm... terms) {
        return makeTuple(terms, null);
    }

    @Override
    public IStrategoTerm parseFromString(String text) throws ParseError {
        return reader.parseFromString(text);
    }

    @Override
    public IStrategoTerm copyAttachments(IStrategoTerm from, IStrategoTerm to) {
        ITermAttachment attach = from.getAttachment(null);
        while(attach != null) {
            try {
                to.putAttachment(attach.clone());
            } catch(CloneNotSupportedException e) {
                throw new IllegalArgumentException(
                    "Copying attachments of this type is not supported: " + attach.getAttachmentType(), e);
            }
            attach = attach.getNext();
        }
        return to;
    }

    @Override
    public IStrategoList.Builder arrayListBuilder() {
        return StrategoArrayList.arrayListBuilder();
    }

    @Override
    public IStrategoList.Builder arrayListBuilder(int initialCapacity) {
        return StrategoArrayList.arrayListBuilder(initialCapacity);
    }


    @Override
    public IStrategoAppl buildAppl(String constructorName, IStrategoTerm[] subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        return buildAppl(new StrategoConstructor(constructorName, subterms.length), subterms, replacee, annotations);
    }

    @Override
    public IStrategoAppl buildAppl(String constructorName, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        return buildAppl(constructorName, termsIterableToArray(subterms), replacee, annotations);
    }

    @Override
    public IStrategoAppl buildAppl(IStrategoConstructor constructor, Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        return buildAppl(constructor, termsIterableToArray(subterms), replacee, annotations);
    }

    @Override
    public IStrategoList buildList(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        return buildList(termsIterableToArray(subterms), replacee, annotations);
    }

    @Override
    public IStrategoTuple buildTuple(Iterable<IStrategoTerm> subterms, @Nullable IStrategoTerm replacee, @Nullable IStrategoList annotations) {
        return buildTuple(termsIterableToArray(subterms), replacee, annotations);
    }



    /**
     * Converts an iterable of terms to an array.
     *
     * @param terms the iterable to convert
     * @return the resulting array
     */
    protected static IStrategoTerm[] termsIterableToArray(Iterable<IStrategoTerm> terms) {
        return IterableUtils.toArray(terms, EMPTY_TERM_ARRAY);
    }
}
