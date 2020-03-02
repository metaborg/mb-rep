package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermPrinter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class StrategoTuple extends StrategoTerm implements IStrategoTuple {

    private static final long serialVersionUID = -6034069486754146955L;

    private final TermList kids;

    public StrategoTuple(IStrategoTerm[] kids, IStrategoList annotations) {
        this(TermList.of(kids), annotations);
    }

    public StrategoTuple(List<IStrategoTerm> kids, IStrategoList annotations) {
        super(annotations);
        this.kids = TermList.fromIterable(kids);
    }

    @Override
    public IStrategoTerm get(int index) {
        return kids.get(index);
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return kids.toArray(new IStrategoTerm[0]);
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return kids;
    }

    @Override
    public int size() {
        return kids.size();
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        return kids.get(index);
    }

    @Override
    public int getSubtermCount() {
        return kids.size();
    }

    @Override
    public int getTermType() {
        return IStrategoTerm.TUPLE;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(second.getTermType() != IStrategoTerm.TUPLE)
            return false;

        IStrategoTuple snd = (IStrategoTuple) second;
        if(size() != snd.size())
            return false;

        List<IStrategoTerm> kids = this.kids;
        List<IStrategoTerm> secondKids = snd.getSubterms();
        if (kids.size() != secondKids.size()) return false;
        if(!kids.equals(secondKids)) {
            for(int i = 0, sz = kids.size(); i < sz; i++) {
                IStrategoTerm kid = kids.get(i);
                IStrategoTerm secondKid = secondKids.get(i);
                if(kid != secondKid && !kid.match(secondKid)) {
                    return false;
                }
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
        if(kids.size() > 0) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                kids.get(0).writeAsString(output, maxDepth - 1);
                for(int i = 1; i < kids.size(); i++) {
                    output.append(',');
                    kids.get(i).writeAsString(output, maxDepth - 1);
                }
            }
        }
        output.append(')');
        appendAnnotations(output, maxDepth);
    }

    @Override
    public int hashFunction() {
        long hc = 4831;
        for (IStrategoTerm kid : this.kids) {
            hc *= kid.hashCode();
        }
        return (int) (hc >> 10);
    }

    @Override
    public Iterator<IStrategoTerm> iterator() {
        return this.kids.iterator();
    }
}
