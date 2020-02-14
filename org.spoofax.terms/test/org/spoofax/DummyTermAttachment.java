package org.spoofax;

import org.spoofax.terms.attachments.AbstractTermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;


/**
 * A dummy term attachment.
 */
public class DummyTermAttachment extends AbstractTermAttachment {

    public static final DummyTermAttachmentType<DummyTermAttachment> Type1 = new DummyTermAttachmentType<>("Type1", DummyTermAttachment.class);
    public static final DummyTermAttachmentType<DummyTermAttachment> Type2 = new DummyTermAttachmentType<>("Type2", DummyTermAttachment.class);
    public static final DummyTermAttachmentType<DummyTermAttachment> Type3 = new DummyTermAttachmentType<>("Type3", DummyTermAttachment.class);
    public static final DummyTermAttachmentType<DummyTermAttachment> Type4 = new DummyTermAttachmentType<>("Type4", DummyTermAttachment.class);

    private final TermAttachmentType<?> attachmentType;

    public DummyTermAttachment(TermAttachmentType<?> attachmentType) {
        this.attachmentType = attachmentType;
    }

    @Override
    public TermAttachmentType<?> getAttachmentType() {
        return this.attachmentType;
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
