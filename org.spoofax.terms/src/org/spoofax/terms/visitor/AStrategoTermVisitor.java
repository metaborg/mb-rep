package org.spoofax.terms.visitor;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoPlaceholder;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoRef;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public abstract class AStrategoTermVisitor implements IStrategoTermVisitor {
    @Override public boolean visit(IStrategoAppl term) {
        return visit((IStrategoTerm) term);
    }

    @Override public boolean visit(IStrategoList term) {
        return visit((IStrategoTerm) term);
    }

    @Override public boolean visit(IStrategoTuple term) {
        return visit((IStrategoTerm) term);
    }

    @Override public void visit(IStrategoInt term) {
        visit((IStrategoTerm) term);
    }

    @Override public void visit(IStrategoReal term) {
        visit((IStrategoTerm) term);
    }

    @Override public void visit(IStrategoString term) {
        visit((IStrategoTerm) term);
    }

    @Override public void visit(IStrategoRef term) {
        visit((IStrategoTerm) term);
    }

    @Override public boolean visit(IStrategoPlaceholder term) {
        return visit((IStrategoTerm) term);
    }

    @Override public abstract boolean visit(IStrategoTerm term);
}
