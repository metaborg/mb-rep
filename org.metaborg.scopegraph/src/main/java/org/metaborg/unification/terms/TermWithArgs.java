package org.metaborg.unification.terms;

import java.util.Arrays;

public abstract class TermWithArgs implements ITerm {

    private final ITerm[] args;
    private final int hashCode;

    public TermWithArgs(ITerm... args) {
        this.args = args;
        this.hashCode = calcHashCode();
    }

    public final ITerm[] getArgs() {
        return args;
    }

    private int calcHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(args);
        return result;
    }

    @Override public int hashCode() {
        return hashCode;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof TermWithArgs))
            return false;
        TermWithArgs other = (TermWithArgs) obj;
        if (!Arrays.equals(args, other.args))
            return false;
        return true;
    }

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < args.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(args[i].toString());
        }
        return sb.toString();
    }

}
