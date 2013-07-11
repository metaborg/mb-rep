package org.spoofax.interpreter.library.index;

import java.util.Date;

import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * A partition in an {@link IIndex} that partitions {@link IndexEntry}s.
 */
public class IndexPartition {
    private final IndexPartitionDescriptor descriptor;

    protected IndexPartition(IndexPartitionDescriptor descriptor, Date time) {
        this.descriptor = descriptor;
    }

    public IndexPartitionDescriptor getDescriptor() {
        return descriptor;
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
