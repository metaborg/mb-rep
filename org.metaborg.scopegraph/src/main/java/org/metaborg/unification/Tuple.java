package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Tuple extends Complex {

    public Tuple(Rep[] args) throws InterpreterException {
        super(args);
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if(r instanceof Var || r instanceof Op) {
            return r.unify(this, reduceOp);
        }
        if(!(r instanceof Tuple)) {
            return UnificationResult.failure(this+" != "+r);
        }
        Tuple t = (Tuple) r;
        return unifys(t, reduceOp);
    }

    @Override public Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        super.finds(reduceOp);
        return this;
    }
    
    @Override public IStrategoTerm toTerm(ITermFactory factory ) {
        return factory.makeTuple(toTerms(factory));
    }

    @Override public String toString() {
        return "("+toStrings()+")";
    }

}