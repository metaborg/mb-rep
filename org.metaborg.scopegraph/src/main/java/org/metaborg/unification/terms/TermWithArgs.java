package org.metaborg.unification.terms;

import org.immutables.value.Value;
import org.metaborg.unification.ITerm;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("serial")
public abstract class TermWithArgs implements ITerm {

    /** Must be redeclared in subclasses, to make it a @Value.Parameter. */
    public abstract ImmutableList<ITerm> getArgs();

    @Value.Lazy public boolean isGround() {
        boolean isGround = true;
        for (ITerm arg : getArgs()) {
            isGround &= arg.isGround();
        }
        return isGround;
    }

    @Override public String toString() {
        StringBuffer sb = new StringBuffer();
        boolean first = true;
        for (ITerm arg : getArgs()) {
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
