package org.metaborg.scopegraph.indices;

import java.util.Collection;

import org.metaborg.scopegraph.INameResolution;
import org.metaborg.scopegraph.context.IScopeGraphContext;
import org.metaborg.scopegraph.context.IScopeGraphUnit;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SG_get_ast_references extends ScopeGraphPrimitive {
 
    public SG_get_ast_references() {
        super(SG_get_ast_references.class.getSimpleName(), 0, 0);
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
        INameResolution nameResolution = unit.nameResolution();
        if(nameResolution == null) {
            return false;
        }
        Collection<IStrategoTerm> indices = nameResolution.astPaths(index);
        env.setCurrent(env.getFactory().makeList(indices));
        return true;
    }

}
