package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.TermType;
import org.spoofax.terms.util.ArrayIterator;
import org.spoofax.terms.util.TermUtils;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class StrategoTuple extends StrategoTerm implements IStrategoTuple {

    private static final long serialVersionUID = -6034069486754146955L;

    private final IStrategoTerm[] kids;

    public StrategoTuple(IStrategoTerm[] kids, @Nullable IStrategoList annotations) {
        super(annotations);
        this.kids = kids;
    }

    @Override
    public IStrategoTerm get(int index) {
        return kids[index];
    }

    public IStrategoTerm[] getAllSubterms() {
        return kids;
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(kids);
    }

    @Override
    public int size() {
        return kids.length;
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        return kids[index];
    }

    @Override
    public int getSubtermCount() {
        return kids.length;
    }

    @Override
    public TermType getType() {
        return TermType.TUPLE;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(!TermUtils.isTuple(second))
            return false;

        IStrategoTuple snd = (IStrategoTuple) second;
        if(size() != snd.size())
            return false;

        IStrategoTerm[] kids = this.kids;
        IStrategoTerm[] secondKids = snd.getAllSubterms();
        if (kids.length != secondKids.length) return false;
        for(int i = 0, sz = kids.length; i < sz; i++) {
            IStrategoTerm kid = kids[i];
            IStrategoTerm secondKid = secondKids[i];
            if(kid != secondKid && !kid.match(secondKid)) {
                return false;
            }
        }

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
    }

    @Deprecated
    @Override
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

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('(');
        IStrategoTerm[] kids = getAllSubterms();
        if(kids.length > 0) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                kids[0].writeAsString(output, maxDepth - 1);
                for(int i = 1; i < kids.length; i++) {
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
        for(int i = 0; i < kids.length; i++) {
            hc *= kids[i].hashCode();
        }
        return (int) (hc >> 10);
    }

    public Iterator<IStrategoTerm> iterator() {
        return new ArrayIterator<IStrategoTerm>(kids);
    }
}
