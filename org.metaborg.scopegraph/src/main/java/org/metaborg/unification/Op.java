package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

class Op implements Rep {

    private Appl op;
    private Rep result;

    public Op(IStrategoConstructor cons, Rep[] args) throws InterpreterException {
        this.op = new Appl(cons, args);
        this.result = null;
    }

    @Override public UnificationResult unify(Rep r, Function<Rep,Rep> reduceOp) throws InterpreterException {
        if(r instanceof Var) {
            return r.unify(this, reduceOp);
        }
        return UnificationResult.defer(this, r);
    }

    @Override public Rep find(Function<Rep, Rep> reduceOp) throws InterpreterException {
        if(result == null) {
            op = op.find(reduceOp);
            if(!op.isGround() || (result = reduceOp.apply(op)) == null) {
                return this;
            }
        }
        return result.find(reduceOp);
    }
 
    @Override public boolean occurs(Var v) {
        return op.occurs(v);
    }
    
    @Override public boolean isGround() {
        return false;
    }
    
    @Override public IStrategoTerm toTerm(ITermFactory factory ) {
        return op.toTerm(factory);
    }

    @Override public String toString() {
        return op.toString();
    }

}