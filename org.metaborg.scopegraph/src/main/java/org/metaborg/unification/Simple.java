package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Simple implements Rep {
    private final IStrategoTerm t;

    Simple(IStrategoTerm t) {
        this.t = t;
    }

    @Override public UnificationResult unify(Rep r, Function<Rep, Rep> reduceOp) throws InterpreterException {
        if(r instanceof Var || r instanceof Op) {
            return r.unify(this, reduceOp);
        }
        if(!(r instanceof Simple)) {
            return UnificationResult.failure(this+" != "+r);
        }
        Simple s = (Simple) r;
        if(!t.match(s.t)) {
            return UnificationResult.failure(this+" != "+r);
        }
        return UnificationResult.success();
    }

    @Override public Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        return this;
    }

    public static Simple find(IStrategoTerm t) {
        return new Simple(t);
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