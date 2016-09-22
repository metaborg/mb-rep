package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Appl extends Complex {

    private static final long serialVersionUID = -1489444856638821449L;

    private final IStrategoConstructor cons;

    public Appl(IStrategoConstructor cons, Rep[] args) {
        super(args);
        this.cons = cons;
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if(r.isActive()) {
            return r.unify(this, reduceOp);
        }
        if(!(r instanceof Appl)) {
            return new UnificationResult(this+" != "+r);
        }
        Appl a = (Appl) r;
        if(!cons.equals(a.cons)) {
            return new UnificationResult(this+" != "+r);
        }
        return unifys(a, reduceOp);
    }

    @Override
    public Appl find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        super.finds(reduceOp);
        return this;
    }
    
    @Override public IStrategoTerm toTerm(ITermFactory factory ) {
        return factory.makeAppl(cons, toTerms(factory));
    }

    @Override public String toString() {
        return cons.getName()+"("+toStrings()+")";
    }

}