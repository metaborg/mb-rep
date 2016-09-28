package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.metaborg.util.iterators.Iterables2;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Pair;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

abstract class Complex implements Rep {

    private static final long serialVersionUID = 3696193702404477518L;

    private final int size;
    private final Rep[] reps;
    private boolean ground;

    protected Complex(Rep[] reps) {
        this.size = reps.length;
        this.reps = reps;
        this.ground = false;
    }

    protected UnificationResult unifys(Complex r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if (size != r.size) {
            return new UnificationResult(this + " differs in length from " + r);
        }
        @SuppressWarnings("unchecked") final Iterable<String>[] errorsArray = new Iterable[size];
        @SuppressWarnings("unchecked") final Iterable<Pair<Rep,Rep>>[] remainingArray = new Iterable[size];
        for (int i = 0; i < size; i++) {
            Rep r1 = reps[i].find(reduceOp);
            Rep r2 = r.reps[i].find(reduceOp);
            UnificationResult result = r1.unify(r2, reduceOp);
            remainingArray[i] = result.progress ? result.remaining : Iterables2.singleton(new Pair<Rep,Rep>(r1, r2));
            errorsArray[i] = result.progress ? result.errors : Iterables2.<String> empty();
        }
        final Iterable<String> errors = Iterables2.fromConcat(errorsArray);
        final Iterable<Pair<Rep,Rep>> remaining = Iterables2.fromConcat(remainingArray);
        return new UnificationResult(true, errors, remaining);
    }

    protected void finds(Function<Rep,Rep> reduceOp) throws InterpreterException {
        boolean newDone = true;
        for (int i = 0; i < size; i++) {
            if (!reps[i].isGround()) {
                reps[i] = reps[i].find(reduceOp);
                ground &= reps[i].isGround();
            }
        }
        ground = newDone;
    }

    @Override public boolean isActive() {
        return false;
    }

    @Override public boolean isGround() {
        return ground;
    }

    @Override public boolean occurs(Var v) {
        boolean occurs = false;
        for (Rep rep : reps) {
            occurs |= rep.occurs(v);
        }
        return occurs;
    }

    protected IStrategoTerm[] toTerms(ITermFactory factory) {
        IStrategoTerm[] elems = new IStrategoTerm[size];
        for (int i = 0; i < size; i++) {
            elems[i] = reps[i].toTerm(factory);
        }
        return elems;
    }

    protected String toStrings() {
        final StringBuilder sb = new StringBuilder();
        boolean tail = false;
        for (Rep r : reps) {
            if (tail) {
                sb.append(",");
            }
            sb.append(r);
            tail = true;
        }
        return sb.toString();
    }

}