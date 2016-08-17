package org.metaborg.scopegraph.indices;

import org.metaborg.scopegraph.context.IScopeGraphContext;
import org.metaborg.scopegraph.context.IScopeGraphUnit;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SG_set_ast_metadata extends ScopeGraphPrimitive {
    
    public SG_set_ast_metadata() {
        super(SG_set_ast_metadata.class.getSimpleName(), 0, 2);
    }


    @Override public boolean call(IScopeGraphContext<?> context, IContext env,
            Strategy[] strategies, IStrategoTerm[] terms)
        throws InterpreterException {
        ITermIndex index = TermIndex.get(env.current());
        if(index == null) {
            throw new InterpreterException("Term has no AST index.");
        }
        IScopeGraphUnit unit = context.unit(index.resource());
        if(unit == null) {
            return false;
        }
        unit.setMetadata(index.nodeId(), terms[0], terms[1]); 
        return true;
    }

}