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
	 * Creates a term representation of given index.
	 *
	 * @param index The index to create a term representation of.
	 * @param factory A term factory.
	 * @param includePositions True to include position information.
	 * @return A term representing given index.
	 */
	public IStrategoTerm indexToTerm(IIndex index, ITermFactory factory, boolean includePositions) {
		IStrategoList partitionsTerm = factory.makeList();
		for(IndexPartition partition : index.getAllPartitions()) {
			final IStrategoTerm partitionTerm = partitionToTerm(index, partition, factory);
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

	private IStrategoTerm partitionToTerm(IIndex index, IndexPartition partition, ITermFactory factory) {
		final IStrategoList entriesTerm = IndexEntry.toTerms(factory, index.getInPartition(partition));
		IStrategoTerm partitionTerm = factory.makeTuple(partition.toTerm(factory), entriesTerm);
		return partitionTerm;
	}


	/**
	 * Populates an index from a term representation of an index created with
	 * {@link #indexToTerm(IIndex, ITermFactory, boolean)}.
	 *
	 * @param index The index to populate.
	 * @param term A term representation of an index.
	 * @param factory A term factory.
	 * @param extractPositions True to also extract position information.
	 */
	public IIndex indexFromTerm(IIndex index, IOAgent agent, IStrategoTerm term, ITermFactory factory,
		boolean extractPositions) {
		if(!Tools.isTermTuple(term))
			throw new RuntimeException("Cannot read index: Root term is not a tuple.");

		IStrategoTerm partitionsTerm = term.getSubterm(0);
		if(extractPositions) {
			final TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			partitionsTerm = serializer.fromAnnotations(partitionsTerm, false);
		}
		for(IStrategoTerm partitionTerm : partitionsTerm)
			partitionFromTerm(index, agent, partitionTerm);

		final IStrategoTerm languagesTerm = term.getSubterm(1);
		for(IStrategoTerm languageTerm : languagesTerm)
			index.addLanguage(((IStrategoString) languageTerm).stringValue());

		return index;
	}

	private IndexPartition partitionFromTerm(IIndex index, IOAgent agent, IStrategoTerm term) {
		if(!Tools.isTermTuple(term))
			throw new RuntimeException("Cannot read index: Partition term is not a tuple.");

		final IndexPartition partition = IndexPartition.fromTerm(agent, term.getSubterm(0));
		for(IStrategoTerm entry : term.getSubterm(1)) {
			index.add(index.getFactory().createEntry((IStrategoAppl) entry, partition));
		}
		return partition;
	}
}
