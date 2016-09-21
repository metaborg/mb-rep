package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

interface Rep {
    UnificationResult unify(Rep r, Function<Rep, Rep> reduceOp) throws InterpreterException;
    Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException;
    boolean occurs(Var v);
    boolean isGround();
    IStrategoTerm toTerm(ITermFactory factory);
}