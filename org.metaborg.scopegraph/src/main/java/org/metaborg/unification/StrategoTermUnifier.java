package org.metaborg.unification;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class StrategoTermUnifier {
    private static ILogger logger = LoggerUtils.logger(StrategoTermUnifier.class);

    private final IContext env;
    private final Map<IStrategoTerm,Var> vars;

    public StrategoTermUnifier(IContext env, Strategy isVar, Strategy isOp) {
        this.env = env;
        this.vars = Maps.newHashMap();
    }

    public boolean unify(IStrategoTerm t1, IStrategoTerm t2) throws InterpreterException {
        Rep r1 = find(t1);
        Rep r2 = find(t2);
        return r1.unify(r2);
    }

    private Rep find(IStrategoTerm t) throws InterpreterException {
        if(isVar(t)) {
            Var v;
            if((v = vars.get(t)) == null) {
                vars.put(t, (v = new Var(t)));
            }
            return v.find();
        } else {
            switch(t.getTermType()) {
            case IStrategoTerm.APPL:
                return new Appl((IStrategoAppl) t);
            case IStrategoTerm.LIST:
                return new List((IStrategoList) t);
            case IStrategoTerm.TUPLE:
                return new Tuple((IStrategoTuple) t);
            case IStrategoTerm.INT:
            case IStrategoTerm.REAL:
            case IStrategoTerm.STRING:
                return new Simple(t);
            default:
                throw new IllegalArgumentException();
            }
        }
    }

    private boolean isVar(IStrategoTerm t) throws InterpreterException {
        if(t instanceof IStrategoAppl) {
            IStrategoAppl a = (IStrategoAppl) t;
            return a.getName().equals("CVar");
        }
        return false;
    }

    private interface Rep {
        boolean unify(Rep r);
        Rep find();
        boolean occurs(Var v);
        boolean done();
        IStrategoTerm toTerm();
    }

    private class Var implements Rep {
        private final IStrategoTerm t;
        private Rep rep;
        private int size;

        public Var(IStrategoTerm t) {
            this.t = t;
            rep = this;
            size = 1;
        }

        @Override public boolean unify(Rep r) {
            if(r instanceof Var) {
                Var v = (Var) r;
                union(this,v);
                return true;
            } else {
                if(r.occurs(this)) {
                    return false;
                }
                rep = r;
                return true;
            }
        }

        private void union(Var v1, Var v2) {
            if(v2.size > v1.size) {
                final Var tmp = v1;
                v1 = v2;
                v2 = tmp;
            }
            v1.size += v2.size;
            v2.rep = v1;
        }

        @Override public Rep find() {
            if(rep != this) {
                rep = rep.find();
            }
            return rep;
        }

        @Override public boolean done() {
            return false;
        }

        @Override public boolean occurs(Var v) {
            return v == this;
        }

        @Override public IStrategoTerm toTerm() {
            return t;
        }

        @Override public String toString() {
            return t.toString();
        }
    }

    private abstract class Complex implements Rep {
        private final int size;
        private final Rep[] reps;
        private boolean done;

        protected Complex(IStrategoTerm[] ts) throws InterpreterException {
            size = ts.length;
            reps = new Rep[size];
            done = false;
            for(int i = 0; i < size; i++) {
                reps[i] = StrategoTermUnifier.this.find(ts[i]);
            }
        }

        protected boolean unifys(Complex r) {
            boolean success = size == r.size;
            for(int i = 0; i < size; i++) {
                success &= reps[i].unify(r.reps[i]);
            }
            return success;
        }

        @Override public Rep find() {
            boolean newDone = true;
            for(int i = 0; i < size; i++) {
                Rep r = reps[i];
                if(!r.done()) {
                    r = reps[i].find();
                    done &= r.done();
                    reps[i] = r;
                }
            }
            done = newDone;
            return this;
        }

        @Override public boolean done() {
            return done;
        }

        @Override public boolean occurs(Var v) {
            boolean occurs = false;
            for(Rep rep : reps) {
                occurs |= rep.occurs(v);
            }
            return occurs;
        }

        protected IStrategoTerm[] toTerms() {
            IStrategoTerm[] elems = new IStrategoTerm[size];
            for(int i = 0; i < size; i++) {
                elems[i] = reps[i].toTerm();
            }
            return elems;
        }

        protected String toStrings() {
            final StringBuilder sb = new StringBuilder();
            boolean tail = false;
            for(Rep r : reps) {
                if(tail) {
                    sb.append(",");
                }
                sb.append(r);
                tail = true;
            }
            return sb.toString();
        }
    }

    private class Appl extends Complex {
        private final IStrategoConstructor cons;

        public Appl(IStrategoAppl t) throws InterpreterException {
            super(t.getAllSubterms());
            cons = t.getConstructor();
        }

        @Override public boolean unify(Rep r) {
            if(r instanceof Var)
                return r.unify(this);
            if(!(r instanceof Appl))
                return false;
            Appl a = (Appl) r;
            if(!cons.equals(a.cons))
                return false;
            return unifys(a);
        }

        @Override public IStrategoTerm toTerm() {
            ITermFactory factory = env.getFactory();
            return factory.makeAppl(cons, toTerms());
        }

        @Override public String toString() {
            return cons.getName()+"("+toStrings()+")";
        }
    }

    private class List extends Complex {
        public List(IStrategoList t) throws InterpreterException {
            super(t.getAllSubterms());
        }

        @Override public boolean unify(Rep r) {
            if(r instanceof Var)
                return r.unify(this);
            if(!(r instanceof List))
                return false;
            List l = (List) r;
            return unifys(l);
        }

        @Override public IStrategoTerm toTerm() {
            ITermFactory factory = env.getFactory();
            return factory.makeList(toTerms());
        }

        @Override public String toString() {
            return "["+toStrings()+"]";
        }
    }

    private class Tuple extends Complex {
        public Tuple(IStrategoTuple t) throws InterpreterException {
            super(t.getAllSubterms());
        }

        @Override public boolean unify(Rep r) {
            if(r instanceof Var)
                return r.unify(this);
            if(!(r instanceof Tuple))
                return false;
            Tuple t = (Tuple) r;
            return unifys(t);
        }

        @Override public IStrategoTerm toTerm() {
            ITermFactory factory = env.getFactory();
            return factory.makeTuple(toTerms());
        }

        @Override public String toString() {
            return "("+toStrings()+")";
        }
    }

    private class Simple implements Rep {
        private final IStrategoTerm t;

        public Simple(IStrategoTerm t) {
            this.t = t;
        }

        @Override public boolean unify(Rep r) {
            if(r instanceof Var)
                return r.unify(this);
            if(!(r instanceof Simple))
                return false;
            Simple s = (Simple) r;
            return t.match(s.t);
        }

        @Override public Rep find() {
            return this;
        }

        @Override public boolean done() {
            return true;
        }

        @Override public boolean occurs(Var v) {
            return false;
        }

        @Override public IStrategoTerm toTerm() {
            return t;
        }

        @Override public String toString() {
            return t.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Stratego Unifier{");
        for(Var v : vars.values()) {
            sb.append("\n\t");
            sb.append(v.toString());
            sb.append(" -> ");
            sb.append(v.find().toString());
        }
        sb.append("\n}");
        return sb.toString();
    }

    public IStrategoTerm toTerm() {
        ITermFactory factory = env.getFactory();
        Collection<Var> vs =  vars.values();
        LinkedList<IStrategoTuple> entries = Lists.newLinkedList();
        for(Var v : vs) {
            entries.add(factory.makeTuple(v.toTerm(), v.find().toTerm()));
        }
        return factory.makeList(entries);
    }

}