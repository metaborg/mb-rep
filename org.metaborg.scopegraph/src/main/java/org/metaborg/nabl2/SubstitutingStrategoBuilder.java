package org.metaborg.nabl2;

import java.util.List;

import org.metaborg.unification.IListTerm;
import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.unification.terms.ATermFunction;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.DoubleTerm;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.NilTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermOp;
import org.metaborg.unification.terms.TermVar;
import org.metaborg.unification.terms.TupleTerm;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public final class SubstitutingStrategoBuilder {

    private static final ILogger logger = LoggerUtils.logger(SubstitutingStrategoBuilder.class);

    private final ITermUnifier unifier;
    private final ITermFactory termFactory;
    private final IStrategoConstructor specialTail;
    private final IStrategoConstructor varConstructor;

    public SubstitutingStrategoBuilder(ITermUnifier unifier, ITermFactory termFactory) {
        this.unifier = unifier.findAll();
        this.termFactory = termFactory;
        this.specialTail = termFactory.makeConstructor("LazyList", 1);
        this.varConstructor = termFactory.makeConstructor("CVar", 2);
    }

    public IStrategoTerm substitution() {
        List<IStrategoTuple> entries = Lists.newLinkedList();
        for (TermVar var : unifier.variables()) {
            ITerm term = unifier.find(var).rep();
            IStrategoTerm varTerm = termVar(var);
            IStrategoTerm resultTerm = term(term);
            entries.add(termFactory.makeTuple(varTerm, resultTerm));
        }
        return termFactory.makeList(entries);
    }

    public IStrategoTerm term(ITerm term) {
        ITerm rep = unifier.find(term).rep();
        return rep.apply(termFunction);
    }

    public IStrategoTerm primitiveTerm(IPrimitiveTerm term) {
        // TODO Fix this mess
        if (term instanceof IntTerm) {
            IntTerm i = (IntTerm) term;
            return termFactory.makeInt(i.getValue());
        } else if (term instanceof DoubleTerm) {
            DoubleTerm i = (DoubleTerm) term;
            return termFactory.makeReal(i.getValue());
        } else if (term instanceof StringTerm) {
            StringTerm i = (StringTerm) term;
            return termFactory.makeString(i.getValue());
        } else {
            throw new IllegalArgumentException("Unsupported primitive term " + term);
        }
    }

    public IStrategoAppl applTerm(ApplTerm term) {
        return termFactory.makeAppl(termFactory.makeConstructor(term.getOp(), term.getArity()), terms(term.getArgs()));
    }

    public IStrategoTuple tupleTerm(TupleTerm term) {
        return termFactory.makeTuple(terms(term.getArgs()));
    }

    public IStrategoList listTerm(IListTerm term) {
        ITerm rep = unifier.find(term).rep();
        return rep.apply(listTermVisitor);
    }

    public IStrategoList consTerm(ConsTerm term) {
        return termFactory.makeListCons(term(term.getHead()), listTerm(term.getTail()));
    }

    public IStrategoList nilTerm(NilTerm term) {
        return termFactory.makeList();
    }

    public IStrategoAppl termVar(TermVar term) {
        IStrategoTerm resource = termFactory.makeString(term.getResource() == null ? "" : term.getResource());
        IStrategoTerm name = termFactory.makeString(term.getName());
        return termFactory.makeAppl(varConstructor, resource, name);
    }

    public IStrategoAppl termOp(TermOp term) {
        return termFactory.makeAppl(termFactory.makeConstructor(term.getOp(), term.getArity()), terms(term.getArgs()));
    }

    private IStrategoTerm[] terms(final ImmutableList<ITerm> terms) {
        final IStrategoTerm[] newTerms = new IStrategoTerm[terms.size()];
        for (int i = 0; i < terms.size(); i++) {
            newTerms[i] = term(terms.get(i));
        }
        return newTerms;
    }

    private final ITermFunction<IStrategoTerm> termFunction = new ATermFunction<IStrategoTerm>() {

        @Override public IStrategoTerm apply(TermVar term) {
            return termVar(term);
        }

        @Override public IStrategoTerm apply(IPrimitiveTerm term) {
            return primitiveTerm(term);
        }

        @Override public IStrategoTerm apply(ApplTerm term) {
            return applTerm(term);
        }

        @Override public IStrategoTerm apply(TupleTerm term) {
            return tupleTerm(term);
        }

        @Override public IStrategoTerm apply(ConsTerm term) {
            return consTerm(term);
        }

        @Override public IStrategoTerm apply(NilTerm term) {
            return nilTerm(term);
        }

        @Override public IStrategoTerm apply(TermOp term) {
            return termOp(term);
        }

        @Override public IStrategoTerm defaultApply(ITerm term) {
            throw new IllegalArgumentException("Unsupported term " + term);
        }

    };

    private final ITermFunction<IStrategoList> listTermVisitor = new ATermFunction<IStrategoList>() {

        @Override public IStrategoList apply(ConsTerm term) {
            return consTerm(term);
        }

        @Override public IStrategoList apply(NilTerm term) {
            return nilTerm(term);
        }

        @Override public IStrategoList apply(TermVar term) {
            logger.warn("Turning list tail " + term + " into last element.");
            return termFactory.makeList(termFactory.makeAppl(specialTail, term(term)));
        }

        @Override public IStrategoList apply(TermOp term) {
            logger.warn("Turning list tail " + term + " into last element.");
            return termFactory.makeList(termFactory.makeAppl(specialTail, term(term)));
        }

        @Override public IStrategoList defaultApply(ITerm term) {
            throw new IllegalArgumentException("Unsupported term " + term);
        }

    };

}
