package org.metaborg.unification;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class U_unify extends AbstractPrimitive {

    public U_unify() {
        super(U_unify.class.getSimpleName(), 0, 1);
    }

    @Override
    public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        final IStrategoTerm unifierTerm = tvars[0];
        if(!(unifierTerm instanceof StrategoUnifierTerm)) {
            throw new InterpreterException("Term argument must be unifier term.");
        }
        StrategoTermUnifier unifier = ((StrategoUnifierTerm) unifierTerm).unifier();

        final IStrategoTerm current = env.current();
        if(!(Tools.isTermTuple(current) && current.getSubtermCount() == 2)) {
            throw new InterpreterException("");
        }
        IStrategoTerm t1 = current.getSubterm(0);
        IStrategoTerm t2 = current.getSubterm(1);

        return unifier.unify(t1, t2);
    }

}