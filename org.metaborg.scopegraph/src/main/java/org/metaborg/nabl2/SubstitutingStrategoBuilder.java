package org.metaborg.nabl2;

import java.util.List;

import org.metaborg.unification.persistent.PersistentTermUnifier;
import org.metaborg.unification.terms.ATermVisitor;
import org.metaborg.unification.terms.ApplTerm;
import org.metaborg.unification.terms.ConsTerm;
import org.metaborg.unification.terms.DoubleTerm;
import org.metaborg.unification.terms.IListTerm;
import org.metaborg.unification.terms.IPrimitiveTerm;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.ITermVisitor;
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

    private final PersistentTermUnifier unifier;
    private final ITermFactory termFactory;
    private final IStrategoConstructor specialTail;
    private final IStrategoConstructor varConstructor;

    public SubstitutingStrategoBuilder(PersistentTermUnifier unifier, ITermFactory termFactory) {
        this.unifier = unifier.findAll();
        this.termFactory = termFactory;
        this.specialTail = termFactory.makeConstructor("LazyList", 1);
        this.varConstructor = termFactory.makeConstructor("CVar", 2);
    }

    public IStrategoTerm substitution() {
        List<IStrategoTuple> entries = Lists.newLinkedList();
        for (TermVar var : unifier.variables()) {
            ITerm term = unifier.find(var).rep;
            IStrategoTerm varTerm = termVar(var);
            IStrategoTerm resultTerm = term(term);
            entries.add(termFactory.makeTuple(varTerm, resultTerm));
        }
        return termFactory.makeList(entries);
    }

    public IStrategoTerm term(ITerm term) {
        ITerm rep = unifier.find(term).rep;
        return rep.accept(termVisitor);
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
        ITerm rep = unifier.find(term).rep;
        return rep.accept(listTermVisitor);
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

    private final ITermVisitor<IStrategoTerm> termVisitor = new ATermVisitor<IStrategoTerm>() {

        @Override public IStrategoTerm visit(TermVar term) {
            return termVar(term);
        }

        @Override public IStrategoTerm visit(IPrimitiveTerm term) {
            return primitiveTerm(term);
        }

        @Override public IStrategoTerm visit(ApplTerm term) {
            return applTerm(term);
        }

        @Override public IStrategoTerm visit(TupleTerm term) {
            return tupleTerm(term);
        }

        @Override public IStrategoTerm visit(ConsTerm term) {
            return consTerm(term);
        }

        @Override public IStrategoTerm visit(NilTerm term) {
            return nilTerm(term);
        }

        @Override public IStrategoTerm visit(TermOp term) {
            return termOp(term);
        }

        @Override public IStrategoTerm visit(ITerm term) {
            throw new IllegalArgumentException("Unsupported term " + term);
        }

    };

    private final ITermVisitor<IStrategoList> listTermVisitor = new ATermVisitor<IStrategoList>() {

        @Override public IStrategoList visit(ConsTerm term) {
            return consTerm(term);
        }

        @Override public IStrategoList visit(NilTerm term) {
            return nilTerm(term);
        }

        @Override public IStrategoList visit(TermVar term) {
            logger.warn("Turning list tail " + term + " into last element.");
            return termFactory.makeList(termFactory.makeAppl(specialTail, term(term)));
        }

        @Override public IStrategoList visit(TermOp term) {
            logger.warn("Turning list tail " + term + " into last element.");
            return termFactory.makeList(termFactory.makeAppl(specialTail, term(term)));
        }

        @Override public IStrategoList visit(ITerm term) {
            throw new IllegalArgumentException("Unsupported term " + term);
        }

    };

}
