package org.spoofax;

import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;


/**
 * A dummy simple term.
 */
public class DummySimpleTerm implements ISimpleTerm {

    private final String name;

    public DummySimpleTerm() {
        this.name = "dummy-" + System.identityHashCode(this);
    }

    public DummySimpleTerm(String name) {
        this.name = name;
    }

    @Override
    public int getSubtermCount() {
        return 0;
    }

    @Override
    public ISimpleTerm getSubterm(int i) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    public <T extends ITermAttachment> T getAttachment(TermAttachmentType<T> type) {
        return null;
    }

    @Override
    public void putAttachment(ITermAttachment resourceAttachment) { /* Ignored */ }

    @Override
    public ITermAttachment removeAttachment(TermAttachmentType<?> attachmentType) {
        return null;
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
        // Force hash collisions.
        // This is a valid (but inefficient) implementation of hashCode(),
        // to ensure code that bails out on hash code inequality will still run.
        return 0;
    }

    @Override public String toString() {
        return "<" + this.name + ">";
    }

}
