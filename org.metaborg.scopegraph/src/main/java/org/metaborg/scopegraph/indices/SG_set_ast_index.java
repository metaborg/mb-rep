package org.metaborg.scopegraph.indices;

import org.metaborg.scopegraph.context.IScopeGraphContext;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SG_set_ast_index extends ScopeGraphPrimitive {
 
    public SG_set_ast_index() {
        super(SG_set_ast_index.class.getSimpleName(), 0, 1);
    }


    @Override public boolean call(IScopeGraphContext<?> context, IContext env,
            Strategy[] strategies, IStrategoTerm[] terms)
        throws InterpreterException {
        TermIndex.put(env.current(),(IStrategoAppl)terms[0]);
        return true;
    }

}