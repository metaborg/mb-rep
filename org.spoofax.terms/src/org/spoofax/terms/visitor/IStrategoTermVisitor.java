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

public interface IStrategoTermVisitor {
    public abstract boolean visit(IStrategoAppl term);

    public abstract boolean visit(IStrategoList term);

    public abstract boolean visit(IStrategoTuple term);

    public abstract void visit(IStrategoInt term);

    public abstract void visit(IStrategoReal term);

    public abstract void visit(IStrategoString term);

    public abstract void visit(IStrategoRef term);

    public abstract boolean visit(IStrategoPlaceholder term);
    
    public abstract boolean visit(IStrategoTerm term);
}
