package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class List extends Complex {

    public List(Rep[] elems) throws InterpreterException {
        super(elems);
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if(r instanceof Var || r instanceof Op) {
            return r.unify(this, reduceOp);
        }
        if(!(r instanceof List)) {
            return UnificationResult.failure(this+" != "+r);
        }
        List l = (List) r;
        return unifys(l, reduceOp);
    }

    @Override public Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        super.finds(reduceOp);
        return this;
    }
    
    @Override public IStrategoTerm toTerm(ITermFactory factory ) {
        return factory.makeList(toTerms(factory));
    }

    @Override public String toString() {
        return "["+toStrings()+"]";
    }

}