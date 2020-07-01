package org.spoofax.terms.skeleton;


import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.AbstractTermFactory;
import org.spoofax.terms.util.NotImplementedException;

public abstract class SkeletonTermFactory extends AbstractTermFactory {

    public SkeletonTermFactory() {
        super();
    }

    final public IStrategoPlaceholder makePlaceholder(IStrategoTerm template) {
        throw new NotImplementedException();
    }

    final public IStrategoReal makeReal(double d) {
        throw new NotImplementedException();
    }

    final public IStrategoTerm annotateTerm(IStrategoTerm term,
            IStrategoList annotations) {
        throw new NotImplementedException();
    }

    final public IStrategoString tryMakeUniqueString(String name) {
        throw new NotImplementedException();
    }

}
