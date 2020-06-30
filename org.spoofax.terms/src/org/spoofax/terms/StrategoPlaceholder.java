package org.spoofax.terms;

import org.spoofax.interpreter.terms.*;

import java.io.IOException;

/**
 * A Stratego Placeholder term.
 */
public class StrategoPlaceholder extends StrategoAppl implements IStrategoPlaceholder {

    private static final long serialVersionUID = -1212433450601997725L;

    public StrategoPlaceholder(IStrategoConstructor ctor, IStrategoTerm template, IStrategoList annotations) {
        super(ctor, new IStrategoTerm[] { template }, annotations);
    }

    @Override
    public IStrategoTerm getTemplate() {
        return getSubterm(0);
    }

    @Override
    public TermType getType() {
        return TermType.PLACEHOLDER;
    }

    @Override
    protected boolean doSlowMatch(IStrategoTerm second) {
        if(second.getType() != TermType.PLACEHOLDER)
            return false;

        if(!getTemplate().match(((IStrategoPlaceholder) second).getTemplate()))
            return false;

        IStrategoList annotations = getAnnotations();
        IStrategoList secondAnnotations = second.getAnnotations();
        if(annotations == secondAnnotations) {
            return true;
        } else
            return annotations.match(secondAnnotations);
    }

    @Override
    public void writeAsString(Appendable output, int maxDepth) throws IOException {
        output.append('<');
        getTemplate().writeAsString(output, maxDepth - 1);
        output.append('>');
        appendAnnotations(output, maxDepth);
    }

    @Override
    @Deprecated
    public void prettyPrint(ITermPrinter pp) {
        pp.print("<");
        getTemplate().prettyPrint(pp);
        pp.print(">");
    }
}
