package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class List extends Complex {

    private static final long serialVersionUID = -5411285359515116313L;

    public List(Rep[] elems) {
        super(elems);
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if (r.isActive()) {
            return r.unify(this, reduceOp);
        }
        if (!(r instanceof List)) {
            return new UnificationResult(this + " != " + r);
        }
        List l = (List) r;
        return unifys(l, reduceOp);
    }

    @Override public Rep find(Function<Rep,Rep> reduceOp) throws InterpreterException {
        super.finds(reduceOp);
        return this;
    }

    @Override public IStrategoTerm toTerm(ITermFactory factory) {
        return factory.makeList(toTerms(factory));
    }

    @Override public String toString() {
        return "[" + toStrings() + "]";
    }

}