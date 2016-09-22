package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.metaborg.unification.mutable.StrategoUnifier.Predicate;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_get_substitution extends AbstractUnifierPrimitive {

    public U_get_substitution() {
        super(U_get_substitution.class.getSimpleName());
    }

    @Override
    protected boolean doCall(IContext env, StrategoUnifier unifier, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep, Rep> reduceOp) throws InterpreterException {
        env.setCurrent(unifier.toTerm(env.getFactory(), reduceOp));
        return true;
    }

}