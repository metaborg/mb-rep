package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Tuple extends Complex {

    private static final long serialVersionUID = 4685267814433460523L;

    public Tuple(Rep[] args) {
        super(args);
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if (r.isActive()) {
            return r.unify(this, reduceOp);
        }
        if (!(r instanceof Tuple)) {
            return new UnificationResult(this + " != " + r);
        }
        Tuple t = (Tuple) r;
        return unifys(t, reduceOp);
    }

    @Override public Rep find(Function<Rep,Rep> reduceOp) throws InterpreterException {
        super.finds(reduceOp);
        return this;
    }

    @Override public IStrategoTerm toTerm(ITermFactory factory) {
        return factory.makeTuple(toTerms(factory));
    }

    @Override public String toString() {
        return "(" + toStrings() + ")";
    }

}