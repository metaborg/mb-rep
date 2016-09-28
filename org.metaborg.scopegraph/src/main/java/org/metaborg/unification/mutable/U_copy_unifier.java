package org.metaborg.unification.mutable;

import org.apache.commons.lang3.SerializationUtils;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_copy_unifier extends AbstractPrimitive {

    public U_copy_unifier() {
        super(U_copy_unifier.class.getSimpleName(), 0, 0);
    }

    @Override public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        final IStrategoTerm unifierTerm = env.current();
        if (!(unifierTerm instanceof StrategoUnifierTerm)) {
            throw new InterpreterException("Term argument must be unifier term.");
        }
        StrategoUnifier unifier = ((StrategoUnifierTerm) unifierTerm).unifier();
        env.setCurrent(new StrategoUnifierTerm(SerializationUtils.clone(unifier)));
        return true;
    }

}