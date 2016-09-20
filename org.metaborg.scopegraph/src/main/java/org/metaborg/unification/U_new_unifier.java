package org.metaborg.unification;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_new_unifier extends AbstractPrimitive {

    public U_new_unifier() {
        super(U_new_unifier.class.getSimpleName(), 2, 0);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        final Strategy isVar = svars[0];
        final Strategy isOp = svars[1];
        final StrategoTermUnifier unifier = new StrategoTermUnifier(env, isVar, isOp);
        final StrategoUnifierTerm term = new StrategoUnifierTerm(unifier);
        env.setCurrent(term);
        return true;
    }

}