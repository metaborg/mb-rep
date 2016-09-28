package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.metaborg.unification.mutable.StrategoUnifier.Predicate;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_get_term extends AbstractUnifierPrimitive {

    public U_get_term() {
        super(U_get_term.class.getSimpleName());
    }

    @Override protected boolean doCall(IContext env, StrategoUnifier unifier, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep,Rep> reduceOp) throws InterpreterException {
        Rep r = unifier.find(env.current(), isVar, isOp, reduceOp);
        if (r.isActive()) {
            return false;
        }
        env.setCurrent(r.toTerm(env.getFactory()));
        return true;
    }

}