package org.metaborg.nabl2;

import java.util.List;

import org.metaborg.unification.IFindResult;
import org.metaborg.unification.IListTerm;
import org.metaborg.unification.IPrimitiveTerm;
import org.metaborg.unification.ITerm;
import org.metaborg.unification.ITermFunction;
import org.metaborg.unification.ITermUnifier;
import org.metaborg.unification.terms.ATermFunction;
import org.metaborg.unification.terms.DoubleTerm;
import org.metaborg.unification.terms.IApplTerm;
import org.metaborg.unification.terms.IConsTerm;
import org.metaborg.unification.terms.INilTerm;
import org.metaborg.unification.terms.ITermOp;
import org.metaborg.unification.terms.ITermVar;
import org.metaborg.unification.terms.ITupleTerm;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.StringTerm;
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

public final class StrategoBuilder {

    private static final ILogger logger = LoggerUtils.logger(StrategoBuilder.class);

    private final ITermFactory termFactory;
    private final IStrategoConstructor specialTail;
    private final IStrategoConstructor varConstructor;

    public StrategoBuilder(ITermFactory termFactory) {
        this.termFactory = termFactory;
        this.specialTail = termFactory.makeConstructor("LazyList", 1);
        this.varConstructor = termFactory.makeConstructor("CVar", 2);
    }

    public IStrategoTerm substitution(final ITermUnifier unifier) {
        List<IStrategoTuple> entries = Lists.newLinkedList();
        ITermUnifier localUnifier = unifier.findAll();
        for (ITermVar var : localUnifier.variables()) {
            IFindResult result = localUnifier.find(var);
            IStrategoTerm varTerm = termVar(var);
            IStrategoTerm resultTerm = term(result.rep());
            entries.add(termFactory.makeTuple(varTerm, resultTerm));
            localUnifier = result.unifier();
        }
        return termFactory.makeList(entries);
    }

    public IStrategoTerm term(ITerm term) {
        return term.apply(termFunction);
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

    public IStrategoAppl applTerm(IApplTerm term) {
        return termFactory.makeAppl(termFactory.makeConstructor(term.getOp(), term.getArity()), terms(term.getArgs()));
    }

    public IStrategoTuple tupleTerm(ITupleTerm term) {
        return termFactory.makeTuple(terms(term.getArgs()));
    }

    public IStrategoList listTerm(IListTerm term) {
        return term.apply(listTermVisitor);
    }

    public IStrategoList consTerm(IConsTerm term) {
        return termFactory.makeListCons(term(term.getHead()), listTerm(term.getTail()));
    }

    public IStrategoList nilTerm(INilTerm term) {
        return termFactory.makeList();
    }

    public IStrategoAppl termVar(ITermVar term) {
        IStrategoTerm resource = termFactory.makeString(term.getResource() == null ? "" : term.getResource());
        IStrategoTerm name = termFactory.makeString(term.getName());
        return termFactory.makeAppl(varConstructor, resource, name);
    }

    public IStrategoAppl termOp(ITermOp term) {
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

        @Override public IStrategoTerm apply(ITermVar term) {
            return termVar(term);
        }

        @Override public IStrategoTerm apply(IPrimitiveTerm term) {
            return primitiveTerm(term);
        }

        @Override public IStrategoTerm apply(IApplTerm term) {
            return applTerm(term);
        }

        @Override public IStrategoTerm apply(ITupleTerm term) {
            return tupleTerm(term);
        }

        @Override public IStrategoTerm apply(IConsTerm term) {
            return consTerm(term);
        }

        @Override public IStrategoTerm apply(INilTerm term) {
            return nilTerm(term);
        }

        @Override public IStrategoTerm apply(ITermOp term) {
            return termOp(term);
        }

        @Override public IStrategoTerm defaultApply(ITerm term) {
            throw new IllegalArgumentException("Unsupported term " + term);
        }

    };

    private final ITermFunction<IStrategoList> listTermVisitor = new ATermFunction<IStrategoList>() {

        @Override public IStrategoList apply(IConsTerm term) {
            return consTerm(term);
        }

        @Override public IStrategoList apply(INilTerm term) {
            return nilTerm(term);
        }

        @Override public IStrategoList apply(ITermVar term) {
            logger.warn("Turning list tail " + term + " into last element.");
            return termFactory.makeList(termFactory.makeAppl(specialTail, termVar(term)));
        }

        @Override public IStrategoList apply(ITermOp term) {
            logger.warn("Turning list tail " + term + " into last element.");
            return termFactory.makeList(termFactory.makeAppl(specialTail, termOp(term)));
        }

        @Override public IStrategoList defaultApply(ITerm term) {
            throw new IllegalArgumentException("Unsupported term " + term);
        }

    };

}