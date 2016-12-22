package org.metaborg.scopegraph.indices;

import java.io.Serializable;

import javax.annotation.Nullable;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.ISimpleTerm;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.AbstractTermAttachment;
import org.spoofax.terms.attachments.TermAttachmentType;

public class TermIndex extends AbstractTermAttachment implements ITermIndex, Serializable {

    private static final long serialVersionUID = 5958528158971840392L;

    public static final TermAttachmentType<TermIndex> TYPE = new TermAttachmentType<TermIndex>(TermIndex.class,
            "TermIndex", 2) {

        @Override protected IStrategoTerm[] toSubterms(ITermFactory factory, TermIndex attachment) {
            return new IStrategoTerm[] { factory.makeString(attachment.resource), factory.makeInt(attachment.nodeId), };
        }

        @Override protected TermIndex fromSubterms(IStrategoTerm[] subterms) {
            return new TermIndex(Tools.asJavaString(subterms[0]), Tools.asJavaInt(subterms[1]));
        }
    };

    private final String resource;
    private final int nodeId;

    private TermIndex(String resource, int nodeId) {
        this.resource = resource;
        this.nodeId = nodeId;
    }


    @Override public TermAttachmentType<TermIndex> getAttachmentType() {
        return TYPE;
    }

    public IStrategoTerm toTerm(ITermFactory factory) {
        return TYPE.toTerm(factory, this);
    }


    public String resource() {
        return resource;
    }

    public int nodeId() {
        return nodeId;
    }


    public static void put(IStrategoTerm term, String resource, int nodeId) {
        term.putAttachment(new TermIndex(resource, nodeId));
    }

    public static boolean put(IStrategoTerm term, IStrategoAppl indexTerm) {
        TermIndex index = TermIndex.TYPE.fromTerm(indexTerm);
        if (index == null) {
            return false;
        }
        term.putAttachment(index);
        return true;
    }

    public static @Nullable TermIndex get(ISimpleTerm term) {
        return term.getAttachment(TYPE);
    }


    @Override public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resource == null) ? 0 : resource.hashCode());
        result = prime * result + nodeId;
        return result;
    }

    @Override public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermIndex other = (TermIndex) obj;
        if (nodeId != other.nodeId)
            return false;
        if (resource == null) {
            if (other.resource != null)
                return false;
        } else if (!resource.equals(other.resource))
            return false;
        return true;
    }

    @Override public String toString() {
        return "@" + resource + ":" + nodeId;
    }

}
