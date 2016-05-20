/*
 * Created on 28. jan.. 2007
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import java.io.IOException;
import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.ArrayIterator;

public class StrategoAppl extends StrategoTerm implements IStrategoAppl {

  private static final long serialVersionUID = -2522680523775044390L;

	private final IStrategoConstructor ctor;

    private IStrategoTerm[] kids;

    public StrategoAppl(IStrategoConstructor ctor, IStrategoTerm[] kids, IStrategoList annotations, int storageType) {
        super(annotations, storageType);
        this.ctor = ctor;
        this.kids = kids;
        
        if (storageType != MUTABLE) initImmutableHashCode();
    }
    
    @Deprecated
    public IStrategoTerm[] getArguments() {
        return kids;
    }

    public IStrategoConstructor getConstructor() {
        return ctor;
    }
    
    public String getName() {
    	return ctor.getName();
    }

    public IStrategoTerm[] getAllSubterms() {
        return kids;
    }

    public IStrategoTerm getSubterm(int index) {
        if (index < 0 || index >= kids.length)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        return kids[index];
    }

    public int getSubtermCount() {
        return kids.length;
    }

    public int getTermType() {
        return IStrategoTerm.APPL;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
        if (second.getTermType() != IStrategoTerm.APPL)
            return false;
        IStrategoAppl o = (IStrategoAppl)second;
        if (!ctor.equals(o.getConstructor()))
            return false;
        
        IStrategoTerm[] kids = getAllSubterms();
        IStrategoTerm[] secondKids = o.getAllSubterms();
        if (kids != secondKids) {
            for (int i = 0, sz = kids.length; i < sz; i++) {
                IStrategoTerm kid = kids[i];
                IStrategoTerm secondKid = secondKids[i];
                if (kid != secondKid && !kid.match(secondKid)) {
                    if (commonStorageType == SHARABLE && i != 0)
                        System.arraycopy(secondKids, 0, kids, 0, i);
                    return false;
                }
            }
            
            if (commonStorageType == SHARABLE)
                this.kids = secondKids;
        }
        
        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if (annotations == secondAnnotations) {
            return true;
        } else if (annotations.match(secondAnnotations)) {
            if (commonStorageType == SHARABLE) internalSetAnnotations(secondAnnotations);
            return true;
        } else {
            return false;
        }
    }

    @Deprecated
    public void prettyPrint(ITermPrinter pp) {
        pp.print(ctor.getName());
        IStrategoTerm[] kids = getAllSubterms();
        if(kids.length > 0) {
            pp.println("(");
            pp.indent(ctor.getName().length());
            kids[0].prettyPrint(pp);
            for(int i = 1; i < kids.length; i++) {
                pp.print(",");
                kids[i].prettyPrint(pp);
            }
            pp.println(")");
            pp.outdent(ctor.getName().length());
        }
        printAnnotations(pp);
    }

    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(ctor.getName());
        IStrategoTerm[] kids = getAllSubterms();
        if(kids.length > 0) {
            output.append('(');
            if (maxDepth == 0) {
            	output.append("...");
            } else {
	            kids[0].writeAsString(output, maxDepth - 1);
	            for(int i = 1; i < kids.length; i++) {
	                output.append(',');
	                kids[i].writeAsString(output, maxDepth - 1);
	            }
            }
            output.append(')');
        }
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        long r = ctor.hashCode();
        int accum = 6673;
        IStrategoTerm[] kids = getAllSubterms();
        for(int i = 0; i < kids.length; i++) {
            r += kids[i].hashCode() * accum;
            accum *= 7703;
        }
        return (int)(r >> 12);
    }

	public Iterator<IStrategoTerm> iterator() {
		return new ArrayIterator<IStrategoTerm>(kids);
	}
}
