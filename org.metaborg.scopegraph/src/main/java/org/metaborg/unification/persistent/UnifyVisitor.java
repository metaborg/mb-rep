package org.metaborg.unification.persistent;

import org.metaborg.unification.terms.ATermVisitor;
import org.metaborg.unification.terms.ITerm;
import org.metaborg.unification.terms.ITermVisitor;
import org.metaborg.unification.terms.IntTerm;
import org.metaborg.unification.terms.StringTerm;
import org.metaborg.unification.terms.TermVar;
import org.spoofax.terms.util.NotImplementedException;

final class UnifyVisitor implements ITermVisitor<UnifyResult> {

    private final TermUnifier unifier;
    private final ITerm second;
    
    public UnifyVisitor(TermUnifier unifier, ITerm second) {
        this.unifier = unifier;
        this.second = second;
    }

    // ***** Var *****

    @Override public UnifyResult visit(final TermVar first) {
        return new UnifyResult(new TermUnifier(unifier.reps.put(first, second)));
    }

    private class VarVisitor<T extends ITerm> extends ATermVisitor<UnifyResult> {
        protected final T first;
        public VarVisitor(T first) { this.first = first; }
        @Override public UnifyResult visit(TermVar second) {
            throw new NotImplementedException("bind");
        }
        @Override public UnifyResult visit(ITerm other) {
            throw new NotImplementedException("fail");
        }
    }

    // ***** Int *****

    @Override public UnifyResult visit(final IntTerm first) {
        return second.accept(new IntVisitor(first));
    }

    private class IntVisitor extends VarVisitor<IntTerm> {
        public IntVisitor(IntTerm first) { super(first); }
        @Override public UnifyResult visit(IntTerm second) {
            if(first.getValue() == second.getValue()) {
                return new UnifyResult(unifier);
            } else {
                throw new NotImplementedException("fail");
            }
        }
    }

    // ***** String *****

    @Override
    public UnifyResult visit(StringTerm first) {
        return second.accept(new StringVisitor(first));
    };

    private class StringVisitor extends VarVisitor<StringTerm> {
        public StringVisitor(StringTerm first) { super(first); }
        @Override public UnifyResult visit(StringTerm second) {
            if(first.getValue().equals(second.getValue())) {
                return new UnifyResult(unifier);
            } else {
                throw new NotImplementedException("fail");
            }
        }
    }

}