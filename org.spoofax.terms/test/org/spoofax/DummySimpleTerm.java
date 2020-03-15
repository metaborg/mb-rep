package org.spoofax;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.ITermAttachment;


/**
 * A dummy simple term.
 */
public class DummySimpleTerm implements ISimpleTerm {

    @Override
    public int getSubtermCount() {
        return 0;
    }

    @Override
    public ISimpleTerm getSubterm(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public ITermAttachment internalGetAttachment() {
        return null;
    }

    @Override
    public void internalSetAttachment(ITermAttachment attachment) {

    }

    @Override
    public boolean isList() {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        // Identity
        return this == obj;
    }

    @Override
    public int hashCode() {
        // Identity
        return System.identityHashCode(this);
    }

}
