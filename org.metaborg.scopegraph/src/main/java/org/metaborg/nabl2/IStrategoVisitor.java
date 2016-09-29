package org.metaborg.nabl2;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTuple;

public interface IStrategoVisitor<T> {

    T visit(IStrategoAppl term);

    T visit(IStrategoList term);

    T visit(IStrategoTuple term);

    T visit(IStrategoInt term);

    T visit(IStrategoReal term);

    T visit(IStrategoString term);

}