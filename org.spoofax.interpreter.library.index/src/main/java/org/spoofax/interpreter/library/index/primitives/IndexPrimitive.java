package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.index.IIndex;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public abstract class IndexPrimitive extends AbstractPrimitive {
    public IndexPrimitive(String name, int svars, int tvars) {
        super(name, svars, tvars);
    }

    @Override public final boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars)
        throws InterpreterException {
        final Object contextObj = env.contextObject();
        if(!(contextObj instanceof IIndexContext)) {
            throw new InterpreterException("Context does not implement IIndexContext, cannot retrieve current index");
        }
        final IIndexContext context = (IIndexContext) env.contextObject();
        final IIndex index = context.index();
        if(index == null) {
            throw new InterpreterException("Index has not been initialized, cannot retrieve current index");
        }
        return call(index, env, svars, tvars);
    }

    public abstract boolean call(IIndex index, IContext env, Strategy[] svars, IStrategoTerm[] tvars)
        throws InterpreterException;
}
