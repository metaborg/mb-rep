package org.spoofax.terms;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.spoofax.NotImplementedException;
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
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.ParseError;
import org.spoofax.terms.io.AbstractIOTermFactory;

public abstract class AbstractTermFactory implements ITermFactory {

	public static final IStrategoList EMPTY_LIST = new StrategoList(null, null, null, IStrategoTerm.MAXIMALLY_SHARED);

    public static final IStrategoTerm[] EMPTY = new IStrategoTerm[0];

    private static final HashMap<StrategoConstructor, StrategoConstructor> asyncCtorCache =
        new HashMap<StrategoConstructor, StrategoConstructor>();
    

    public boolean hasConstructor(String ctorName, int arity) {
    	throw new UnsupportedOperationException();
    }

    public StrategoConstructor makeConstructor(String name, int arity) {
        StrategoConstructor result = new StrategoConstructor(name, arity);
        synchronized (TermFactory.class) {
	        StrategoConstructor cached = asyncCtorCache.get(result);
	        if (cached == null) {
	            asyncCtorCache.put(result, result);
	        } else {
	            result = cached;
	        }
        }
        return result;
    }

    public abstract IStrategoAppl makeAppl(IStrategoConstructor constructor,
			IStrategoTerm[] kids, IStrategoList annotations);

    public abstract IStrategoTuple makeTuple(
			IStrategoTerm[] kids, IStrategoList annotations);

    public abstract IStrategoList makeList(
			IStrategoTerm[] kids, IStrategoList annotations);

    public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids,
            IStrategoAppl old) {
        return makeAppl(constructor, kids, old.getAnnotations());
    }

	public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
        return makeTuple(kids, old.getAnnotations());
    }
    
    public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old) {
        return makeList(kids, old.getAnnotations());
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids,
            IStrategoList annotations) {
        return makeAppl(ctr, kids.getAllSubterms(), annotations);
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids) {
        return makeAppl(ctr, kids, null);
    }

    public final IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... terms) {
        return makeAppl(ctr, terms, null);
    }

    public final IStrategoList makeList(IStrategoTerm... terms) {
        return makeList(terms, null);
    }

    public IStrategoList makeList(Collection<IStrategoTerm> terms) {
        return makeList(terms.toArray(EMPTY));
    }
    
    @Deprecated
    public final IStrategoList makeList(IStrategoTerm head, IStrategoList tail) {
        return makeListCons(head, tail);
    }

    public final IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail) {
        return makeListCons (head, tail, null);
    }

    public abstract IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail, IStrategoList annos);

	public final IStrategoTuple makeTuple(IStrategoTerm... terms) {
        return makeTuple(terms, null);
    }
    
    public IStrategoTerm parseFromString(String text) {
    	return reader.parseFromString(text);
    }

}
