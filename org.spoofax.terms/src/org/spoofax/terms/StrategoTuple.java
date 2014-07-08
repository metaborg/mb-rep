/*
 * Created on 9. okt.. 2006
 *
 * Copyright (c) 2005, Karl Trygve Kalleberg <karltk near strategoxt.org>
 * 
 * Licensed under the GNU Lesser General Public License, v2.1
 */
package org.spoofax.terms;

import java.io.IOException;
import java.util.Iterator;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.ArrayIterator;

public class StrategoTuple extends StrategoTerm implements IStrategoTuple {

    private static final long serialVersionUID = -6034069486754146955L;
	
    private IStrategoTerm[] kids;
    
    public StrategoTuple(IStrategoTerm[] kids, IStrategoList annotations, int storageType) {
        super(annotations, storageType);
        this.kids = kids;
        
        // (not pre-initializing hash code here; tuples are mostly short-lived)
        // if (storageType != MUTABLE) initImmutableHashCode();
    }
    
    public IStrategoTerm get(int index) {
        return kids[index];
    }

    public IStrategoTerm[] getAllSubterms() {
        IStrategoTerm[] r = new IStrategoTerm[kids.length];
        System.arraycopy(kids, 0, r, 0, kids.length);
        return r;
    }
    
    public int size() {
        return kids.length;
    }

    public IStrategoTerm getSubterm(int index) {
        return kids[index];
    }

    public int getSubtermCount() {
        return kids.length;
    }

    public int getTermType() {
        return IStrategoTerm.TUPLE;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second, int commonStorageType) {
        if (second.getTermType() != IStrategoTerm.TUPLE)
            return false;

        IStrategoTuple snd = (IStrategoTuple) second;
        if (size() != snd.size())
            return false;
        
        IStrategoTerm[] kids = this.kids;
        IStrategoTerm[] secondKids = snd.getAllSubterms();
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
        int sz = size();
        if(sz > 0) {
            pp.println("(");
            pp.indent(2);
            get(0).prettyPrint(pp);
            for(int i = 1; i < sz; i++) {
                pp.print(",");
                pp.nextIndentOff();
                get(i).prettyPrint(pp);
                pp.println("");
            }
            pp.println("");
            pp.print(")");
            pp.outdent(2);

        } else {
            pp.print("()");
        }
        printAnnotations(pp);
    }
    
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('(');
        IStrategoTerm[] kids = getAllSubterms();
		if (kids.length > 0) {
			if (maxDepth == 0) {
				output.append("...");
			} else {
				kids[0].writeAsString(output, maxDepth - 1);
				for (int i = 1; i < kids.length; i++) {
					output.append(',');
					kids[i].writeAsString(output, maxDepth - 1);
				}
			}
		}
        output.append(')');
        appendAnnotations(output, maxDepth);
    }
    
    @Override
    public int hashFunction() {
        long hc = 4831;
        IStrategoTerm[] kids = getAllSubterms();
        for(int i=0; i< kids.length;i++) {
            hc *= kids[i].hashCode();
        }
        return (int)(hc >> 10);
    }

	public Iterator<IStrategoTerm> iterator() {
		return new ArrayIterator<IStrategoTerm>(kids);
	}
}
