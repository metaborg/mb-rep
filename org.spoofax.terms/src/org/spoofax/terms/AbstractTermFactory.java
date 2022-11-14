package org.spoofax.terms;

import java.util.Collection;
import java.util.HashMap;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.io.TAFTermReader;

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

    @Override public abstract IStrategoAppl makeAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
                                                     @Nullable IStrategoList annotations);

    @Override public abstract IStrategoTuple makeTuple(IStrategoTerm[] kids, @Nullable IStrategoList annotations);

    @Override public abstract IStrategoList makeList(IStrategoTerm[] kids, @Nullable IStrategoList annotations);

    @Override public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old) {
        return makeAppl(constructor, kids, old.getAnnotations());
    }

    @Override public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
        return makeTuple(kids, old.getAnnotations());
    }

    @Override public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old) {
        return makeList(kids, old.getAnnotations());
    }

    @Override public IStrategoList replaceListCons(IStrategoTerm head, IStrategoList tail, IStrategoTerm oldHead,
        IStrategoList oldTail) {
        return makeListCons(head, tail);
    }

    @Override public IStrategoTerm replaceTerm(IStrategoTerm term, IStrategoTerm old) {
        return term;
    }

    @Override public IStrategoPlaceholder replacePlaceholder(IStrategoTerm template, IStrategoPlaceholder old) {
        return makePlaceholder(template);
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids, @Nullable IStrategoList annotations) {
        return makeAppl(ctr, kids.getAllSubterms(), annotations);
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids) {
        return makeAppl(ctr, kids, null);
    }

    @Override public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... terms) {
        return makeAppl(ctr, terms, null);
    }

    @Override public final IStrategoList makeList(IStrategoTerm... terms) {
        // TODO: test if we can just use StrategoArrayList here (maybe copy the array to make sure it's not modifiable)
        return makeList(terms, null);
    }

    @Override public IStrategoList makeList() {
        return new StrategoArrayList();
    }

    public IStrategoList makeList(Collection<? extends IStrategoTerm> terms) {
        return StrategoArrayList.fromCollection(terms);
    }

    @Override public final IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail) {
        return makeListCons(head, tail, null);
    }

    @Override public abstract IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, @Nullable IStrategoList annos);

    @Override public final IStrategoTuple makeTuple(IStrategoTerm... terms) {
        return makeTuple(terms, null);
    }

    @Override public IStrategoTerm parseFromString(String text) throws ParseError {
        return reader.parseFromString(text);
    }

    @Override public IStrategoTerm copyAttachments(IStrategoTerm from, IStrategoTerm to) {
        return staticCopyAttachments(from, to);
    }

    public static IStrategoTerm staticCopyAttachments(IStrategoTerm from, IStrategoTerm to) {
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

    @Override public IStrategoList.Builder arrayListBuilder() {
        return StrategoArrayList.arrayListBuilder();
    }

    @Override public IStrategoList.Builder arrayListBuilder(int initialCapacity) {
        return StrategoArrayList.arrayListBuilder(initialCapacity);
    }
}
