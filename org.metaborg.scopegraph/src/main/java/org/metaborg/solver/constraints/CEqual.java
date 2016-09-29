package org.metaborg.solver.constraints;

import org.metaborg.unification.terms.ITerm;

public class CEqual implements IConstraint {

    private static final long serialVersionUID = 3290973729084863334L;

    public final ITerm term1;
    public final ITerm term2;

    public CEqual(ITerm term1, ITerm term2) {
        this.term1 = term1;
        this.term2 = term2;
    }

    @Override public <T> T accept(IConstraintVisitor<T> visitor) {
        return visitor.visit(this);
    }

    @Override public String toString() {
        return term1 + " == " + term2;
    }

}