package org.metaborg.unification.mutable;

import java.util.Collection;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.metaborg.unification.mutable.StrategoUnifier.Predicate;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Pair;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;

public class U_unify extends AbstractUnifierPrimitive {

    public U_unify() {
        super(U_unify.class.getSimpleName());
    }

    @Override
    protected boolean doCall(IContext env, StrategoUnifier unifier, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep, Rep> reduceOp) throws InterpreterException {
        final ITermFactory factory = env.getFactory();
        final IStrategoTerm current = env.current();
        if(!(Tools.isTermTuple(current) && current.getSubtermCount() == 2)) {
            throw new InterpreterException("Input term is not a 2-tuple.");
        }
        final IStrategoTerm t1 = current.getSubterm(0);
        final IStrategoTerm t2 = current.getSubterm(1);

        UnificationResult result = unifier.unify(t1, t2, isVar, isOp, reduceOp);
        if(!result.progress) {
            return false;
        }
        
        Collection<IStrategoString> errors = Lists.newLinkedList();
        for(String error : result.errors) {
            errors.add(factory.makeString(error));
        }
        IStrategoList errorList = factory.makeList(errors);

        Collection<IStrategoTuple> deferred = Lists.newLinkedList();
        for(Pair<Rep,Rep> remain : result.remaining) {
            deferred.add(factory.makeTuple(remain.first.toTerm(factory), remain.second.toTerm(factory)));
        }
        IStrategoList deferredList = factory.makeList(deferred);

        env.setCurrent(factory.makeTuple(errorList, deferredList));
        return true;
    }
    
}