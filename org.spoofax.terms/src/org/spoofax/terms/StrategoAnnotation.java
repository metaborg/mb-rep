package org.spoofax.terms;

import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.interpreter.terms.ITermPrinter;

import java.io.IOException;

/**
 * Wraps any term with annotations.
 * <p>
 * Uses its own set of attachments, rather than
 * the attachments of the wrapped term.
 *
 * @author Lennart Kats <lennart add lclnet.nl>
 * @see ITermFactory#annotateTerm(IStrategoTerm, IStrategoList)
 * Should generally be used to efficiently annotate a term.
 */
public class StrategoAnnotation extends StrategoWrapped {

    private static final long serialVersionUID = 2918341202178665547L;

    private final ITermFactory factory;

    public StrategoAnnotation(ITermFactory factory, IStrategoTerm term, IStrategoList annotations) {
        super(term, annotations);

        if(!term.getAnnotations().isEmpty())
            throw new IllegalArgumentException("Annotated term cannot already have annotations");

        this.factory = factory;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        IStrategoTerm wrapped = getWrapped();
        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();

        if(annotations != secondAnnotations && !annotations.match(secondAnnotations)) {
            return false;
        }

        if(!annotations.isEmpty()) {
            second = factory.annotateTerm(second, TermFactory.EMPTY_LIST);
        }
        return wrapped.match(second);
    }

    @Override
    protected int hashFunction() {
        assert getWrapped().getAnnotations().isEmpty() : "Constructor contract broken";
        return getWrapped().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        try {
            getWrapped().writeAsString(result, -1);
            appendAnnotations(result, -1);
        } catch(IOException e) {
            throw new RuntimeException(e); // shan't happen
        }
        return result.toString();
    }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        getWrapped().writeAsString(output, maxDepth);
        appendAnnotations(output, maxDepth);
    }

    @Override
    @Deprecated
    public void prettyPrint(ITermPrinter pp) {
        getWrapped().prettyPrint(pp);
        printAnnotations(pp);
    }

    @Override
    @Deprecated
    public IStrategoList prepend(IStrategoTerm prefix) {
        throw new UnsupportedOperationException();
    }

}
