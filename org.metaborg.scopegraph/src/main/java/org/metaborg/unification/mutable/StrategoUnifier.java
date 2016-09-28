package org.metaborg.unification.mutable;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.WeakHashMap;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StrategoUnifier implements Serializable {

    private static final long serialVersionUID = -3900893130902705101L;

    private final Map<IStrategoTerm,Var> vars;
    private transient Map<IStrategoTerm,Rep> terms;

    public StrategoUnifier() {
        this.vars = Maps.newHashMap();
    }

    public UnificationResult unify(IStrategoTerm t1, IStrategoTerm t2, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep,Rep> reduceOp) throws InterpreterException {
        Rep r1 = find(t1, isVar, isOp, reduceOp);
        Rep r2 = find(t2, isVar, isOp, reduceOp);
        return r1.unify(r2, reduceOp);
    }


    Rep find(final IStrategoTerm t, Predicate<IStrategoTerm> isVar, Predicate<IStrategoAppl> isOp,
            Function<Rep,Rep> reduceOp) throws InterpreterException {
        if (isVar.test(t)) {
            return findVar(t, reduceOp);
        } else {
            return findTerm(t, isVar, isOp, reduceOp);
        }
    }

    private Rep findVar(final IStrategoTerm t, Function<Rep,Rep> reduceOp) throws InterpreterException {
        Var v;
        if ((v = vars.get(t)) == null) {
            vars.put(t, (v = new Var(t)));
        }
        return v.find(reduceOp);
    }

    private Rep findTerm(final IStrategoTerm t, Predicate<IStrategoTerm> isVar, Predicate<IStrategoAppl> isOp,
            Function<Rep,Rep> reduceOp) throws InterpreterException {
        Rep r;
        if (terms == null) {
            terms = new WeakHashMap<>();
        }
        if ((r = terms.get(t)) == null) {
            switch (t.getTermType()) {
            case IStrategoTerm.APPL:
                IStrategoAppl at = (IStrategoAppl) t;
                Appl a = new Appl(at.getConstructor(), find(at.getAllSubterms(), isVar, isOp, reduceOp));
                if (isOp.test(at)) {
                    r = new Op(a).find(reduceOp);
                } else {
                    r = a;
                }
                break;
            case IStrategoTerm.LIST:
                IStrategoList l = (IStrategoList) t;
                r = new List(find(l.getAllSubterms(), isVar, isOp, reduceOp));
                break;
            case IStrategoTerm.TUPLE:
                IStrategoTuple p = (IStrategoTuple) t;
                r = new Tuple(find(p.getAllSubterms(), isVar, isOp, reduceOp));
                break;
            case IStrategoTerm.INT:
            case IStrategoTerm.REAL:
            case IStrategoTerm.STRING:
                r = new Simple(t);
                break;
            default:
                throw new IllegalArgumentException();
            }
        }
        return r.find(reduceOp);
    }

    private Rep[] find(IStrategoTerm[] ts, Predicate<IStrategoTerm> isVar, Predicate<IStrategoAppl> isOp,
            Function<Rep,Rep> reduceOp) throws InterpreterException {
        final int size = ts.length;
        Rep[] reps = new Rep[size];
        for (int i = 0; i < size; i++) {
            reps[i] = find(ts[i], isVar, isOp, reduceOp);
        }
        return reps;
    }


    public IStrategoTerm toTerm(ITermFactory factory, Function<Rep,Rep> reduceOp) throws InterpreterException {
        Collection<Var> vs = vars.values();
        LinkedList<IStrategoTuple> entries = Lists.newLinkedList();
        for (Var v : vs) {
            entries.add(factory.makeTuple(v.toTerm(factory), v.find(reduceOp).toTerm(factory)));
        }
        return factory.makeList(entries);
    }


    public interface Predicate<T> {

        boolean test(T term) throws InterpreterException;
    }

    public interface Function<T, R> {

        R apply(T t) throws InterpreterException;
    }

}