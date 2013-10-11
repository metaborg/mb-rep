package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

public class IndexFactory {
	/**
	 * Creates a term representation of given partition.
	 *
	 * @param index The index that contains the partition.
	 * @param partition The partition to create a term representation of.
	 * @param factory A term factory.
	 * @param includePositions True to include position information.
	 * @return A term representing given partition.
	 */
	public IStrategoTerm toTerm(IIndex index, IndexPartition partition, ITermFactory factory, boolean includePositions) {
		final IStrategoList entriesTerm = IndexEntry.toTerms(factory, index.getInPartition(partition));
		IStrategoTerm partitionTerm = factory.makeTuple(partition.toTerm(factory), entriesTerm);

		if(includePositions) {
			final TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			partitionTerm = serializer.toAnnotations(partitionTerm);
		}

		return partitionTerm;
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
		IStrategoList partitionsTerm = factory.makeList();
		for(IndexPartition partition : index.getAllPartitions()) {
			final IStrategoTerm partitionTerm = toTerm(index, partition, factory, false);
			partitionsTerm = factory.makeListCons(partitionTerm, partitionsTerm);
		}

		if(includePositions) {
			final TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			partitionsTerm = (IStrategoList) serializer.toAnnotations(partitionsTerm);
		}

		IStrategoList languagesTerm = factory.makeList();
		for(String language : index.getAllLanguages()) {
			final IStrategoString languageTerm = factory.makeString(language);
			languagesTerm = factory.makeListCons(languageTerm, languagesTerm);
		}


		return factory.makeTuple(partitionsTerm, languagesTerm);
	}

	public IndexPartition partitionFromTerms(IIndex index, IOAgent agent, IStrategoTerm term, ITermFactory factory,
		boolean extractPositions) {
		if(!Tools.isTermTuple(term))
			throw new RuntimeException("Cannot read index: Partition term is not a tuple.");

		if(extractPositions) {
			final TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			term = serializer.fromAnnotations(term, false);
		}

		final IndexPartition partition = IndexPartition.fromTerm(agent, term.getSubterm(0));
		for(IStrategoTerm entry : term.getSubterm(1))
			index.add(index.getFactory().createEntry((IStrategoAppl) entry, partition));
		return partition;
	}

	/**
	 * Populates an index from a term representation of an index created with
	 * {@link #toTerm(IIndex, ITermFactory, boolean)}.
	 *
	 * @param index The index to populate.
	 * @param term A term representation of an index.
	 * @param factory A term factory.
	 * @param extractPositions True to also extract position information.
	 */
	public IIndex indexFromTerms(IIndex index, IOAgent agent, IStrategoTerm term, ITermFactory factory,
		boolean extractPositions) {
		if(!Tools.isTermTuple(term))
			throw new RuntimeException("Cannot read index: Root term is not a tuple.");

		IStrategoTerm partitionsTerm = term.getSubterm(0);
		if(extractPositions) {
			final TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			partitionsTerm = serializer.fromAnnotations(partitionsTerm, false);
		}
		for(IStrategoTerm partitionTerm : partitionsTerm)
			partitionFromTerms(index, agent, partitionTerm, factory, false);

		final IStrategoTerm languagesTerm = term.getSubterm(1);
		for(IStrategoTerm languageTerm : languagesTerm)
			index.addLanguage(((IStrategoString) languageTerm).stringValue());

		return index;
	}
}
