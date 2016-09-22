package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Var implements Rep {

    private static final long serialVersionUID = 6653758246621007795L;

    private final IStrategoTerm t;
    private Rep rep;
    private int size;

    public Var(IStrategoTerm t) {
        this.t = t;
        rep = this;
        size = 1;
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) {
        if(r instanceof Var) {
            Var v = (Var) r;
            union(this,v);
        } else {
            if(r.occurs(this)) {
                return new UnificationResult(this+" occurs in "+r);
            }
            rep = r;
        }
        return new UnificationResult(true);
    }

    private void union(Var v1, Var v2) {
        if(v2.size > v1.size) {
            final Var tmp = v1;
            v1 = v2;
            v2 = tmp;
        }
        v1.size += v2.size;
        v2.rep = v1;
    }

    @Override public Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        if(rep != this) {
            rep = rep.find(reduceOp);
        }
        return rep;
    }

    @Override public boolean isActive() {
        return true;
    }

    @Override public boolean isGround() {
        return false;
    }

    @Override public boolean occurs(Var v) {
        return v == this;
    }

    @Override public IStrategoTerm toTerm(ITermFactory factory) {
        return t;
    }

    @Override public String toString() {
        return t.toString();
    }

}