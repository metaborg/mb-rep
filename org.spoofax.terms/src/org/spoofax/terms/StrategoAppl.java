package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.interpreter.terms.TermType;
import org.spoofax.terms.util.ArrayIterator;
import org.spoofax.terms.util.TermUtils;

import jakarta.annotation.Nullable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class StrategoAppl extends StrategoTerm implements IStrategoAppl {

    private static final long serialVersionUID = -2522680523775044390L;

    private final IStrategoConstructor ctor;

    private final IStrategoTerm[] kids;

    public StrategoAppl(IStrategoConstructor ctor, IStrategoTerm[] kids, @Nullable IStrategoList annotations) {
        super(annotations);
        this.ctor = ctor;
        this.kids = kids;
    }

    @Deprecated
    public IStrategoTerm[] getArguments() {
        return kids;
    }

    @Override
    public IStrategoConstructor getConstructor() {
        return ctor;
    }

    @Override
    public String getName() {
        return ctor.getName();
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(kids);
    }

    @Override
    public IStrategoTerm getSubterm(int index) {
        if(index < 0 || index >= kids.length)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        return kids[index];
    }

    @Override
    public IStrategoTerm[] getAllSubterms() {
        return kids;
    }

    @Override
    public int getSubtermCount() {
        return kids.length;
    }

    @Override
    public TermType getType() {
        return TermType.APPL;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(!TermUtils.isAppl(second))
            return false;
        IStrategoAppl o = (IStrategoAppl) second;
        if(!ctor.equals(o.getConstructor()))
            return false;

        IStrategoTerm[] kids = getAllSubterms();
        IStrategoTerm[] secondKids = o.getAllSubterms();
        if (kids.length != secondKids.length) return false;
        if(!Arrays.equals(kids, secondKids)) {
            for(int i = 0, sz = kids.length; i < sz; i++) {
                IStrategoTerm kid = kids[i];
                IStrategoTerm secondKid = secondKids[i];
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

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append(ctor.getName());
        IStrategoTerm[] kids = getAllSubterms();
        if(kids.length > 0) {
            output.append('(');
            if(maxDepth == 0) {
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
        return (int) (r >> 12);
    }

    public Iterator<IStrategoTerm> iterator() {
        return new ArrayIterator<IStrategoTerm>(kids);
    }
}
