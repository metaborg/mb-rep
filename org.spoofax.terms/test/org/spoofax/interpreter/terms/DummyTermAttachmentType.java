package org.spoofax.interpreter.terms;

import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;


/* package private */ class DummyTermAttachmentType<T extends ITermAttachment> extends TermAttachmentType<T> {

    private final String name;

    /* package private */ DummyTermAttachmentType(String name, Class<T> type) {
        super(type, null, 0);
        this.name = name;
    }

    @Override
    protected IStrategoTerm[] toSubterms(ITermFactory factory, T attachment) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected T fromSubterms(IStrategoTerm[] subterms) {
        throw new UnsupportedOperationException();
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

    @Override
    public String toString() {
        return "DummyTermAttachmentType{" +
                "name='" + name + '\'' +
                '}';
    }

}
