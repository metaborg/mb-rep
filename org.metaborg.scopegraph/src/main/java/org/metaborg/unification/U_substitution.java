package org.metaborg.unification;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_substitution extends AbstractPrimitive {

    public U_substitution() {
        super(U_substitution.class.getSimpleName(), 0, 0);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        final IStrategoTerm unifierTerm = env.current();
        if(!(unifierTerm instanceof StrategoUnifierTerm)) {
            throw new InterpreterException("Term argument must be unifier term.");
        }
        StrategoTermUnifier unifier = ((StrategoUnifierTerm) unifierTerm).unifier();
        env.setCurrent(unifier.toTerm());
        return true;
    }

}