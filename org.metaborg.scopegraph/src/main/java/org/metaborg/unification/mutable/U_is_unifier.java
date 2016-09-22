package org.metaborg.unification.mutable;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_is_unifier extends AbstractPrimitive {

    public U_is_unifier() {
        super(U_is_unifier.class.getSimpleName(), 0, 0);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        return env.current() instanceof StrategoUnifierTerm;
    }

}