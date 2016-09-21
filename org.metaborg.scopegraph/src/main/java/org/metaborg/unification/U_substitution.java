package org.metaborg.unification;

import org.metaborg.unification.StrategoUnifier.Function;
import org.metaborg.unification.StrategoUnifier.Predicate;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_substitution extends AbstractUnifierPrimitive {

    public U_substitution() {
        super(U_substitution.class.getSimpleName());
    }

    @Override
    protected boolean doCall(IContext env, StrategoUnifier unifier, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep, Rep> reduceOp) throws InterpreterException {
        env.setCurrent(unifier.toTerm(env.getFactory(), reduceOp));
        return true;
    }

}