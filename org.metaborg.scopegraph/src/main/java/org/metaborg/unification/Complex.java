package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

abstract class Complex implements Rep {
    private final int size;
    private final Rep[] reps;
    private boolean ground;

    protected Complex(Rep[] reps) throws InterpreterException {
        this.size = reps.length;
        this.reps = reps;
        this.ground = false;
    }

    protected UnificationResult unifys(Complex r, Function<Rep, Rep> reduceOp) throws InterpreterException {
        if(size != r.size) {
            return UnificationResult.failure(this+" differs in length from "+r);
        }
        UnificationResult[] results = new UnificationResult[size];
        for(int i = 0; i < size; i++) {
            results[i] = reps[i].find(reduceOp).unify(r.reps[i].find(reduceOp), reduceOp);
        }
        return UnificationResult.combine(results);
    }

    protected void finds(Function<Rep, Rep> reduceOp) throws InterpreterException {
        boolean newDone = true;
        for(int i = 0; i < size; i++) {
            if(!reps[i].isGround()) {
                reps[i] = reps[i].find(reduceOp);
                ground &= reps[i].isGround();
            }
        }
        ground = newDone;
    }

    @Override public boolean isGround() {
        return ground;
    }

    @Override public boolean occurs(Var v) {
        boolean occurs = false;
        for(Rep rep : reps) {
            occurs |= rep.occurs(v);
        }
        return occurs;
    }

    protected IStrategoTerm[] toTerms(ITermFactory factory) {
        IStrategoTerm[] elems = new IStrategoTerm[size];
        for(int i = 0; i < size; i++) {
            elems[i] = reps[i].toTerm(factory);
        }
        return elems;
    }

    protected String toStrings() {
        final StringBuilder sb = new StringBuilder();
        boolean tail = false;
        for(Rep r : reps) {
            if(tail) {
                sb.append(",");
            }
            sb.append(r);
            tail = true;
        }
        return sb.toString();
    }
}