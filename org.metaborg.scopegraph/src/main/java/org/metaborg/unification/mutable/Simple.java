package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Simple implements Rep {

    private static final long serialVersionUID = -6665390796739299987L;

    private final IStrategoTerm t;

    Simple(IStrategoTerm t) {
        this.t = t;
    }

    @Override public UnificationResult unify(Rep r, Function<Rep, Rep> reduceOp) throws InterpreterException {
        if(r.isActive()) {
            return r.unify(this, reduceOp);
        }
        if(!(r instanceof Simple)) {
            return new UnificationResult(this+" != "+r);
        }
        Simple s = (Simple) r;
        if(!t.match(s.t)) {
            return new UnificationResult(this+" != "+r);
        }
        return new UnificationResult(true);
    }

    @Override public Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        return this;
    }

    public static Simple find(IStrategoTerm t) {
        return new Simple(t);
    }
    
    @Override public boolean isActive() {
        return false;
    }

    @Override public boolean isGround() {
        return true;
    }

    @Override public boolean occurs(Var v) {
        return false;
    }

    @Override public IStrategoTerm toTerm(ITermFactory factory) {
        return t;
    }

    @Override public String toString() {
        return t.toString();
    }

}