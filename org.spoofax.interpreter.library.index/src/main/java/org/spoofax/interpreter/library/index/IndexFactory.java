package org.spoofax.interpreter.library.index;

import static org.spoofax.interpreter.core.Tools.isTermList;
import static org.spoofax.terms.Term.termAt;
import static org.spoofax.terms.Term.tryGetConstructor;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

public class IndexFactory {
    private static final IStrategoConstructor PARTITION_ENTRIES_CONSTRUCTOR = new TermFactory().makeConstructor(
        "PartitionEntries", 2);

    /**
     * Creates a term representation of given partition.
     * 
     * @param index The index that contains the partition.
     * @param partition The partition to create a term representation of.
     * @param factory A term factory.
     * @param includePositions True to include position information.
     * @return A term representing given partition.
     */
    public IStrategoTerm toTerm(IIndex index, IndexPartitionDescriptor partition, ITermFactory factory,
        boolean includePositions) {
        IStrategoList results = IndexEntry.toTerms(factory, index.getInPartition(partition));
        // TODO: include time stamp & revision for partition
        IStrategoTerm partitionResult =
            factory.makeAppl(PARTITION_ENTRIES_CONSTRUCTOR, partition.toTerm(factory), results);

        if (includePositions) {
            TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
            partitionResult = serializer.toAnnotations(partitionResult);
        }

        return partitionResult;
    }

    /**
     * Creates a term representation of given index.
     * 
     * @param index The index to create a term representation of.
     * @param factory A term factory.
     * @param includePositions True to include position information.
     * @return A term representing given index.
     */
    public IStrategoTerm toTerm(IIndex index, ITermFactory factory, boolean includePositions) {
        IStrategoList results = factory.makeList();
        for (IndexPartitionDescriptor partition : index.getAllPartitionDescriptors()) {
            IStrategoTerm result = toTerm(index, partition, factory, false);
            results = factory.makeListCons(result, results);
        }

        if (includePositions) {
            TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
            results = (IStrategoList) serializer.toAnnotations(results);
        }

        return results;
    }

    public IndexPartitionDescriptor partitionFromTerms(IIndex index, IStrategoTerm term, ITermFactory factory,
        boolean extractPositions) throws IOException {
        if (tryGetConstructor(term) == PARTITION_ENTRIES_CONSTRUCTOR) {
            try {
                if (extractPositions) {
                    TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
                    term = serializer.fromAnnotations(term, false);
                }

                IndexPartitionDescriptor partition = index.getPartitionDescriptor(termAt(term, 0));
                index.addAll((IStrategoList) termAt(term, 1), partition);
                return partition;
            } catch (IllegalStateException e) {
                throw new IllegalStateException(e);
            } catch (RuntimeException e) { // HACK: catch all runtime exceptions
                throw new IOException("Unexpected exception reading index: " + e);
            }
        } else {
            throw new IOException("Illegal index entry: " + term);
        }
    }

    /**
     * Populates an index from a term representation of an index created with
     * {@link #toTerm(IIndex, ITermFactory, boolean)}.
     * 
     * @param index The index to populate.
     * @param term A term representation of an index.
     * @param factory A term factory.
     * @param extractPositions True to also extract position information.
     * @return
     * @throws IOException
     */
    public IIndex indexFromTerms(IIndex index, IStrategoTerm term, ITermFactory factory, boolean extractPositions)
        throws IOException {
        if (extractPositions) {
            TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
            term = serializer.fromAnnotations(term, false);
        }

        if (isTermList(term)) {
            for (IStrategoList list = (IStrategoList) term; !list.isEmpty(); list = list.tail()) {
                partitionFromTerms(index, list.head(), factory, false);
            }
            return index;
        } else {
            throw new IOException("Expected list of " + PARTITION_ENTRIES_CONSTRUCTOR.getName());
        }
    }
}
