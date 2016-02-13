package org.spoofax.interpreter.library.index.primitives.legacy;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class RedirectAbstractPrimitive extends AbstractPrimitive {
    private final AbstractPrimitive redirectTo;

    public RedirectAbstractPrimitive(String name, AbstractPrimitive redirectTo) {
        super(name, redirectTo.getSArity(), redirectTo.getTArity());
        this.redirectTo = redirectTo;
    }

    @Override public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
        return redirectTo.call(env, svars, tvars);
    }
}
