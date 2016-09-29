package org.metaborg.nabl2;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public abstract class AStrategoVisitor<T> implements IStrategoVisitor<T> {

    @Override public T visit(IStrategoAppl term) {
        return visit((IStrategoTerm) term);
    }

    @Override public T visit(IStrategoList term) {
        return visit((IStrategoTerm) term);
    }

    @Override public T visit(IStrategoTuple term) {
        return visit((IStrategoTerm) term);
    }

    @Override public T visit(IStrategoInt term) {
        return visit((IStrategoTerm) term);
    }

    @Override public T visit(IStrategoReal term) {
        return visit((IStrategoTerm) term);
    }

    @Override public T visit(IStrategoString term) {
        return visit((IStrategoTerm) term);
    }

    public abstract T visit(IStrategoTerm term);

}
