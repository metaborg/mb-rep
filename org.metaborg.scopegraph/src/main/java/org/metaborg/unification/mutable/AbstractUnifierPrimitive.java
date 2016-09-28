package org.metaborg.unification.mutable;

import org.metaborg.unification.mutable.StrategoUnifier.Function;
import org.metaborg.unification.mutable.StrategoUnifier.Predicate;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

public abstract class AbstractUnifierPrimitive extends AbstractPrimitive {

    public AbstractUnifierPrimitive(final String name) {
        super(name, 3, 1);
    }

    @Override public boolean call(final IContext env, Strategy[] svars, IStrategoTerm[] tvars)
            throws InterpreterException {
        final Strategy isVar = svars[0];
        final Strategy isOp = svars[1];
        final Strategy reduceOp = svars[2];

        final IStrategoTerm unifierTerm = tvars[0];
        if (!(unifierTerm instanceof StrategoUnifierTerm)) {
            throw new InterpreterException("Term argument must be unifier term.");
        }
        final StrategoUnifier unifier = ((StrategoUnifierTerm) unifierTerm).unifier();

        final Predicate<IStrategoTerm> isVarP = new Predicate<IStrategoTerm>() {

            @Override public boolean test(final IStrategoTerm t) throws InterpreterException {
                env.setCurrent(t);
                return isVar.evaluate(env);
            }
        };
        final Predicate<IStrategoAppl> isOpP = new Predicate<IStrategoAppl>() {

            @Override public boolean test(IStrategoAppl a) throws InterpreterException {
                env.setCurrent(a);
                return isOp.evaluate(env);
            }
        };
        final Function<Rep,Rep> reduceOpF = new Function<Rep,Rep>() {

            @Override public Rep apply(Rep r) throws InterpreterException {
                env.setCurrent(r.toTerm(env.getFactory()));
                if (reduceOp.evaluate(env)) {
                    return unifier.find(env.current(), isVarP, isOpP, this);
                }
                return null;
            }
        };
        return doCall(env, unifier, isVarP, isOpP, reduceOpF);
    }

    protected abstract boolean doCall(IContext env, StrategoUnifier unifier, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep,Rep> reduceOp) throws InterpreterException;

}