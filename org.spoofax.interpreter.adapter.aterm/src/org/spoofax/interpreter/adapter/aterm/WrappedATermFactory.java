/*
 * Created on 29.jul.2005
 *
 * Copyright (c) 2004, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU General Public License, v2
 */
package org.spoofax.interpreter.adapter.aterm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
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

import aterm.AFun;
import aterm.ATerm;
import aterm.ATermAppl;
import aterm.ATermInt;
import aterm.ATermList;
import aterm.ATermReal;

public class WrappedATermFactory implements ITermFactory {
	private final IStrategoList EMPTY_LIST; 
	
    private final TrackingATermFactory realFactory;
    private final WeakHashMap<ATerm, WrappedATerm> termCache;
    private final WeakHashMap<AFun, IStrategoConstructor> ctorCache;
    
    public TrackingATermFactory getFactory() {
    	return realFactory;
    }

    public WrappedATermFactory() {
        realFactory = new TrackingATermFactory();
        termCache = new WeakHashMap<ATerm, WrappedATerm>();
        ctorCache = new WeakHashMap<AFun, IStrategoConstructor>();
        EMPTY_LIST = makeList(new IStrategoTerm[0]);
    }
    
    public boolean hasConstructor(String name, int arity) {
        return realFactory.hasAFun(name, arity);
    }

    public IStrategoTerm parseFromFile(String path) throws IOException {
        ATerm t = realFactory.readFromFile(path);
        return wrapTerm(t);
    }

    public IStrategoTerm parseFromStream(InputStream inputStream) throws IOException {
        ATerm t = realFactory.readFromFile(inputStream);
        return wrapTerm(t);
    }

    public IStrategoTerm parseFromString(String text) {
        ATerm t = realFactory.parse(text);
        return wrapTerm(t);
    }

    public WrappedATerm wrapTerm(ATerm t) {
        
        WrappedATerm r = getInterned(t);
        if(r != null)
            return r;
        
        switch(t.getType()) {
        case ATerm.AFUN:
            return setInterned(t, new WrappedAFun(this, (AFun)t));
        case ATerm.REAL:
            return setInterned(t, new WrappedATermReal(this, (ATermReal)t));
        case ATerm.INT:
            return setInterned(t, new WrappedATermInt(this, (ATermInt)t));
        case ATerm.LIST:
            return setInterned(t, new WrappedATermList(this, (ATermList)t));
        case ATerm.APPL:
            ATermAppl a = (ATermAppl)t;
            if(a.isQuoted() && a.getArity() == 0)
                return setInterned(t, new WrappedATermString(this, a));
            else if(a.getName().equals("")) // FIXME use AFun
                return setInterned(t, new WrappedATermTuple(this, a));
            else  
                return setInterned(t, new WrappedATermAppl(this, a));
        }
        throw new WrapperException();
    }

    protected <T extends WrappedATerm> T setInterned(ATerm t, T wrapper) {
        termCache.put(t, wrapper);
        return wrapper;
    }
    
    protected WrappedATerm getInterned(ATerm t) {
        return termCache.get(t);
    }

    public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoList kids) {
        return ctr.instantiate(this, kids);
    }

    public IStrategoAppl makeAppl(IStrategoConstructor ctr, IStrategoTerm... kids) {
        return ctr.instantiate(this, kids);
    }
    
    public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
        throw new NotImplementedException();
    }

    public IStrategoConstructor makeConstructor(String name, int arity) {
        return wrapConstructor(realFactory.makeAFun(name, arity, false));
    }

    public IStrategoInt makeInt(int i) {
        ATermInt x = realFactory.makeInt(i);
        return setInterned(x, new WrappedATermInt(this, x));
    }
    
    public IStrategoList makeList() {
        return EMPTY_LIST;
    }

    public IStrategoList makeList(IStrategoTerm... terms) {
        ATermList l = realFactory.makeList();
        
        for(int i = terms.length - 1; i >= 0; i--) {
            IStrategoTerm t = terms[i];
            if(t instanceof WrappedATerm) {
                l = l.insert(((WrappedATerm)t).getATerm());
            } else {
                throw new WrapperException();
            }
        }

        IStrategoList r = (IStrategoList) lookupTerm(l);
        if(r != null)
            return r;

        return setInterned(l, new WrappedATermList(this, l));
    }

    private IStrategoTerm lookupTerm(ATermList l) {
        return termCache.get(l);
    }

    public IStrategoList makeList(Collection<IStrategoTerm> terms) {
        return makeList(terms.toArray(new IStrategoTerm[0]));
    }
   
    @Deprecated
    public final IStrategoList makeList(IStrategoTerm head, IStrategoList tail) {
        return makeListCons(head, tail);
    }
   
    public IStrategoList makeListCons(IStrategoTerm head, IStrategoList tail) {
        // TODO: Handle list prepending in factory
        return tail.prepend(head);
    }

    public IStrategoReal makeReal(double d) {
        return (IStrategoReal) wrapTerm(realFactory.makeReal(d));
    }

    public IStrategoString makeString(String s) {
        return (IStrategoString) wrapTerm(realFactory.makeString(s));
    }

    public IStrategoTuple makeTuple(IStrategoTerm... terms) {
        ATerm[] args = new ATerm[terms.length];
        int pos = 0;
        for(IStrategoTerm t : terms) {
            if (t instanceof WrappedATerm) {
            	args[pos++] = ((WrappedATerm)t).getATerm();
            } else {
                throw new WrapperException();
            }
        }
        AFun afun = realFactory.makeAFun("", terms.length, false);
        return (IStrategoTuple) wrapTerm(realFactory.makeAppl(afun, args));
    }

    public void unparseToFile(IStrategoTerm t, OutputStream ous) throws IOException {
        if(!(t instanceof WrappedATerm)) {
            throw new NotImplementedException();
        }

    	if (!(t instanceof WrappedATerm))
    		throw new WrapperException();
    	((WrappedATerm)t).getATerm().writeToTextFile(ous);
    }

    @Deprecated
    public void unparseToFile(IStrategoTerm t, final Writer out) throws IOException {
        ByteArrayOutputStream ous = new ByteArrayOutputStream();
        unparseToFile(t, out);
        out.write(ous.toString());
    }

    public IStrategoConstructor wrapConstructor(AFun fun) {
        
        IStrategoConstructor c = lookupCtor(fun);
        if(c != null)
            return c;
        c = new WrappedAFun(this, fun);
        ctorCache.put(fun, c);
        return c;
    }

    private IStrategoConstructor lookupCtor(AFun fun) {
        return ctorCache.get(fun);
//        IStrategoConstructor c = (IStrategoConstructor) ctorCache.get(fun);
//        if(c == null)
//            System.out.println()
    }

    public IStrategoAppl replaceAppl(IStrategoConstructor constructor, IStrategoTerm[] kids, IStrategoAppl old) {
        return makeAppl(constructor, kids);
    }
    
    public IStrategoList replaceList(IStrategoTerm[] kids, IStrategoList old) {
        return makeList(kids);
    }
    
    public IStrategoTuple replaceTuple(IStrategoTerm[] kids, IStrategoTuple old) {
        return makeTuple(kids);
    }
    
    public IStrategoTerm annotateTerm(IStrategoTerm term, IStrategoList annotations) {
    	if (!(term instanceof WrappedATerm))
    		throw new WrapperException();
    	
    	if (!(annotations instanceof WrappedATerm))
    		throw new WrapperException();
    	
    	ATerm innerTerm = ((WrappedATerm) term).getATerm();
    	ATermList annos = (ATermList) ((WrappedATerm) annotations).getATerm();
    	
    	return wrapTerm(innerTerm.setAnnotations(annos));
    }
}
