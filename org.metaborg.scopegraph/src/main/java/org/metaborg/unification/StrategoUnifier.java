package org.metaborg.unification;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StrategoUnifier {

    private final HashMap<IStrategoTerm,Var> vars;

    public StrategoUnifier() {
        this.vars = Maps.newHashMap();
    }


    public UnificationResult unify(IStrategoTerm t1, IStrategoTerm t2, Predicate<IStrategoTerm> isVar,
            Predicate<IStrategoAppl> isOp, Function<Rep, Rep> reduceOp) throws InterpreterException {
        Rep r1 = find(t1, isVar, isOp, reduceOp);
        Rep r2 = find(t2, isVar, isOp, reduceOp);
        return r1.unify(r2, reduceOp);
    }


    Rep find(IStrategoTerm t, Predicate<IStrategoTerm> isVar, Predicate<IStrategoAppl> isOp,
            Function<Rep, Rep> reduceOp) throws InterpreterException {
        if(isVar.test(t)) {
            Var v;
            if((v = vars.get(t)) == null) {
                vars.put(t, (v = new Var(t)));
            }
            return v.find(reduceOp);
        } else {
            switch(t.getTermType()) {
            case IStrategoTerm.APPL:
                IStrategoAppl a = (IStrategoAppl) t;
                if(isOp.test(a)) {
                    return new Op(a.getConstructor(), find(a.getAllSubterms(), isVar, isOp, reduceOp)).find(reduceOp);
                } else {
                    return new Appl(a.getConstructor(), find(a.getAllSubterms(), isVar, isOp, reduceOp));
                }
            case IStrategoTerm.LIST:
                IStrategoList l = (IStrategoList) t;
                return new List(find(l.getAllSubterms(), isVar, isOp, reduceOp));
            case IStrategoTerm.TUPLE:
                IStrategoTuple p = (IStrategoTuple) t;
                return new Tuple(find(p.getAllSubterms(), isVar, isOp, reduceOp));
            case IStrategoTerm.INT:
            case IStrategoTerm.REAL:
            case IStrategoTerm.STRING:
                return new Simple(t);
            default:
                throw new IllegalArgumentException();
            }
        }
    }

    private Rep[] find(IStrategoTerm[] ts, Predicate<IStrategoTerm> isVar, Predicate<IStrategoAppl> isOp,
            Function<Rep, Rep> reduceOp) throws InterpreterException {
        final int size = ts.length;
        Rep[] reps = new Rep[size];
        for(int i = 0; i < size; i++) {
            reps[i] = find(ts[i], isVar, isOp, reduceOp);
        }
        return reps;
    }
    

    public IStrategoTerm toTerm(ITermFactory factory, Function<Rep, Rep> reduceOp) throws InterpreterException {
        Collection<Var> vs =  vars.values();
        LinkedList<IStrategoTuple> entries = Lists.newLinkedList();
        for(Var v : vs) {
            entries.add(factory.makeTuple(v.toTerm(factory), v.find(reduceOp).toTerm(factory)));
        }
        return factory.makeList(entries);
    }

    public interface Predicate<T> {
        boolean test(T term) throws InterpreterException;
    }
    
    public interface Function<T,R> {
        R apply(T t) throws InterpreterException;
    }
    
}