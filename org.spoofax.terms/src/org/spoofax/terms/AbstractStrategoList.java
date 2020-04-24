package org.spoofax.terms;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermPrinter;
import org.spoofax.terms.util.TermUtils;

public abstract class AbstractStrategoList extends StrategoTerm implements IStrategoList {
    public AbstractStrategoList(IStrategoList annotations) {
        super(annotations);
    }

    @Override
    protected int hashFunction() {
        if(isEmpty())
            return 1;

        final int prime = 31;
        int result = prime * head().hashCode();

        Iterator<IStrategoTerm> iterator = this.iterator();
        for(iterator.next(); iterator.hasNext(); ) {
            IStrategoTerm head = iterator.next();
            result = prime * result + head.hashCode();
        }

        return result;
    }

    @Deprecated @Override public void prettyPrint(ITermPrinter pp) {
        if(!isEmpty()) {
            pp.println("[");
            pp.indent(2);
            Iterator<IStrategoTerm> iter = iterator();
            iter.next().prettyPrint(pp);
            while(iter.hasNext()) {
                IStrategoTerm element = iter.next();
                pp.print(",");
                pp.nextIndentOff();
                element.prettyPrint(pp);
                pp.println("");
            }
            pp.println("");
            pp.print("]");
            pp.outdent(2);

        } else {
            pp.print("[]");
        }
        printAnnotations(pp);
    }

    @Override public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('[');
        if(!isEmpty()) {
            if(maxDepth == 0) {
                output.append("...");
            } else {
                Iterator<IStrategoTerm> iter = iterator();
                iter.next().writeAsString(output, maxDepth - 1);
                while(iter.hasNext()) {
                    IStrategoTerm element = iter.next();
                    output.append(',');
                    element.writeAsString(output, maxDepth - 1);
                }
            }
        }
        output.append(']');
        appendAnnotations(output, maxDepth);
    }

    @Override
    public List<IStrategoTerm> getSubterms() {
        return TermList.ofUnsafe(getAllSubterms());
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(this == second) {
            return true;
        }
        if(!TermUtils.isList(second)) {
            return false;
        }
        if(this.getSubtermCount() != second.getSubtermCount()) {
            return false;
        }

        final IStrategoList snd = (IStrategoList) second;

        if(!isEmpty()) {
            IStrategoTerm head = head();
            IStrategoTerm head2 = snd.head();
            if(head != head2 && !head.match(head2))
                return false;

            IStrategoList tail = tail();
            IStrategoList tail2 = snd.tail();

            for(IStrategoList cons = tail, cons2 = tail2; !cons.isEmpty(); cons = cons.tail(), cons2 = cons2.tail()) {
                IStrategoTerm consHead = cons.head();
                IStrategoTerm cons2Head = cons2.head();
                if(!cons.getAnnotations().match(cons2.getAnnotations())) {
                    return false;
                }
                if(consHead != cons2Head && !consHead.match(cons2Head))
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
}
