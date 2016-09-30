package org.metaborg.nabl2;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.DoubleTerm;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.NilTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermVar;
import org.metaborg.unification.terms.TupleTerm;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoInt;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoReal;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TermBuilder implements IStrategoVisitor<ITerm> {

    public ITerm build(IStrategoTerm term) {
        return StrategoVisitors.accept(term, this);
    }

    @Override public ITerm visit(IStrategoAppl term) {
        switch (term.getName()) {
        case "CVar": {
            IStrategoTerm resource = term.getSubterm(0);
            IStrategoTerm name = term.getSubterm(1);
            return new TermVar(Tools.asJavaString(resource), Tools.asJavaString(name));
        }
        // add lists
        case "TList": {
            IStrategoList elems = (IStrategoList) term.getSubterm(0);
            return makeList(elems, new NilTerm());
        }
        case "TListTail": {
            IStrategoList elems = (IStrategoList) term.getSubterm(0);
            IStrategoTerm tail = term.getSubterm(1);
            return makeList(elems, (IListTerm) build(tail));
        }
        default: {
            return new ApplTerm(term.getName(), visits(term));
        }
        }
    }

    private IListTerm makeList(IStrategoList elems, IListTerm tail) {
        if (elems.isEmpty()) {
            return tail;
        } else {
            return new ConsTerm(build(elems.head()), makeList(elems.tail(), tail));
        }

    }

    @Override public IListTerm visit(IStrategoList term) {
        if (term.isEmpty()) {
            return new NilTerm();
        } else {
            return new ConsTerm(build(term.head()), visit(term.tail()));
        }
    }

    @Override public ITerm visit(IStrategoTuple term) {
        return new TupleTerm(visits(term));
    }

    @Override public ITerm visit(IStrategoInt term) {
        return new IntTerm(term.intValue());

    }

    @Override public ITerm visit(IStrategoReal term) {
        return new DoubleTerm(term.realValue());
    }

    @Override public ITerm visit(IStrategoString term) {
        return new StringTerm(term.stringValue());
    }

    private ImmutableList<ITerm> visits(Iterable<IStrategoTerm> terms) {
        Builder<ITerm> newTerms = ImmutableList.builder();
        for (IStrategoTerm term : terms) {
            newTerms.add(build(term));
        }
        return newTerms.build();
    }

}