package org.metaborg.unification.terms;

import org.metaborg.unification.ITerm;

import com.google.common.collect.ImmutableList;

public abstract class TermWithArgs implements ITerm {

    private static final long serialVersionUID = -4024524039271970100L;

    private final ImmutableList<ITerm> args;
    private final boolean isGround;
    private final int hashCode;

    public TermWithArgs(ImmutableList<ITerm> args) {
        this.args = args;
        this.isGround = calcGround();
        this.hashCode = calcHashCode();
    }

    public final ImmutableList<ITerm> getArgs() {
        return args;
    }

    public final boolean areArgsGround() {
        return isGround;
    }

    public boolean isGround() {
        return areArgsGround();
    }

    private boolean calcGround() {
        boolean isGround = true;
        for (ITerm arg : args) {
            isGround &= arg.isGround();
        }
        return isGround;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + args.hashCode();
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TermWithArgs))
            return false;
        TermWithArgs other = (TermWithArgs) obj;
        if (!args.equals(other.args))
            return false;
        return true;
    }

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (ITerm arg : args) {
            if (first) {
                first = false;
            } else {
                sb.append(",");
            }
            sb.append(arg.toString());
        }
        return sb.toString();
    }

}
