package org.spoofax.interpreter.library.language;

import java.util.Date;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Gabriël Konat
 */
public class IndexPartition {
    private final IndexPartitionDescriptor descriptor;

    private long revision;
    private Date time;

    protected IndexPartition(IndexPartitionDescriptor descriptor, Date time) {
        this.descriptor = descriptor;
        this.time = time;
    }

    public IndexPartitionDescriptor getDescriptor() {
        return descriptor;
    }

    public long getRevision() {
        return revision;
    }

    public Date getTime() {
        return time;
    }

    public void setRevisionTime(long revision, Date time) {
        this.time = time;
        this.revision = revision;
    }

    public IStrategoTerm toTerm(ITermFactory factory) {
        return descriptor.toTerm(factory);
    }

    @Override
    public String toString() {
        return descriptor.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof IndexPartition) {
            if(obj == this)
                return true;
            IndexPartition other = (IndexPartition) obj;
            return other.descriptor.equals(descriptor);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + descriptor.hashCode();
        return result;
    }
}
