package org.metaborg.unification.mutable;

import java.io.Serializable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

interface Rep extends Serializable {

    UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException;

    Rep find(Function<Rep,Rep> reduceOp) throws InterpreterException;

    boolean occurs(Var v);

    boolean isActive();

    boolean isGround();

    IStrategoTerm toTerm(ITermFactory factory);
}