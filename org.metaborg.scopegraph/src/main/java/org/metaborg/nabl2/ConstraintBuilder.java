package org.metaborg.nabl2;

import org.metaborg.solver.constraints.CConj;
import org.metaborg.solver.constraints.CDisj;
import org.metaborg.solver.constraints.CEqual;
import org.metaborg.solver.constraints.CFalse;
import org.metaborg.solver.constraints.CTrue;
import org.metaborg.solver.constraints.IConstraint;
import org.metaborg.unification.ITerm;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class ConstraintBuilder extends AStrategoVisitor<IConstraint> {

    private static final ILogger logger = LoggerUtils.logger(ConstraintBuilder.class);

    private final TermBuilder termBuilder;

    public ConstraintBuilder(TermBuilder termBuilder) {
        this.termBuilder = termBuilder;
    }

    public IConstraint build(IStrategoTerm term) {
        return StrategoVisitors.accept(term, this);
    }

    @Override public IConstraint visit(IStrategoAppl term) {
        // TODO Replace by allowing to register conversion functions
        if (Tools.hasConstructor(term, "CTrue", 0)) {
            return new CTrue();
        } else if (Tools.hasConstructor(term, "CFalse", 1)) {
            return new CFalse();
        } else if (Tools.hasConstructor(term, "CConj", 1)) {
            ImmutableList<IConstraint> constraints = convert(term.getSubterm(0));
            return new CConj(constraints);
        } else if (Tools.hasConstructor(term, "CDisj", 1)) {
            ImmutableList<IConstraint> constraints = convert(term.getSubterm(0));
            return new CDisj(constraints);
        } else if (Tools.hasConstructor(term, "CEqual", 3)) {
            ITerm term1 = termBuilder.build(term.getSubterm(0));
            ITerm term2 = termBuilder.build(term.getSubterm(1));
            return new CEqual(term1, term2);
        } else {
            logger.warn("Ignoring unsupported constraint " + term);
            return new CTrue();
        }
    }

    @Override public IConstraint visit(IStrategoTerm term) {
        throw new IllegalArgumentException(term + " is not a valid constraint.");
    }

    private ImmutableList<IConstraint> convert(Iterable<IStrategoTerm> terms) {
        Builder<IConstraint> constraints = ImmutableList.builder();
        for (IStrategoTerm term : terms) {
            constraints.add(build(term));
        }
        return constraints.build();
    }

}
