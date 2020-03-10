package org.spoofax;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.ITermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;


/**
 * A dummy term attachment type.
 *
 * @param <T> the type of term attachment
 */
public class DummyTermAttachmentType<T extends ITermAttachment> extends TermAttachmentType<T> {

    private final String name;

    public DummyTermAttachmentType(String name, Class<T> type) {
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
