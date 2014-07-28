package org.spoofax.interpreter.library.index;

import java.util.Collection;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

import com.google.common.collect.Lists;

public class IndexFactory {
	private final ITermFactory termFactory;
	private final IndexEntryFactory entryFactory;

	public IndexFactory(ITermFactory termFactory, IndexEntryFactory entryFactory) {
		this.termFactory = termFactory;
		this.entryFactory = entryFactory;
	}

	/**
	 * Creates a term representation of given index.
	 *
	 * @param index The index to create a term representation of.
	 * @param termFactory A term factory.
	 *
	 * @return A term representing given index.
	 */
	public IStrategoTerm indexToTerm(IIndex index) {
		IStrategoList sourcesTerm = termFactory.makeList();
		for(IStrategoTerm source : index.getAllSources()) {
			final IStrategoTerm sourceTerm = sourceToTerm(index, source);
			sourcesTerm = termFactory.makeListCons(sourceTerm, sourcesTerm);
		}

		IStrategoList languageList = termFactory.makeList();
		for(String language : index.getAllLanguages()) {
			final IStrategoString languageTerm = termFactory.makeString(language);
			languageList = termFactory.makeListCons(languageTerm, languageList);
		}

		return termFactory.makeTuple(sourcesTerm, languageList);
	}

	private IStrategoTerm sourceToTerm(IIndex index, IStrategoTerm source) {
		IStrategoList entryList = termFactory.makeList();
		for(IndexEntry entry : index.getInSource(source)) {
			entryList = termFactory.makeListCons(entryFactory.toTerm(entry), entryList);
		}
		return termFactory.makeTuple(source, entryList);
	}

	/**
	 * Populates an index from a term representation of an index created with
	 * {@link #indexToTerm(IIndex, ITermFactory, boolean)}.
	 *
	 * @param index The index to populate.
	 * @param term A term representation of an index.
	 */
	public IIndex indexFromTerm(IIndex index, IStrategoTerm term) {
		if(!Tools.isTermTuple(term))
			throw new RuntimeException("Cannot read index: Root term is not a tuple.");

		final IStrategoTerm sourcesTerm = term.getSubterm(0);
		for(IStrategoTerm sourceTerm : sourcesTerm)
			sourceFromTerm(index, sourceTerm);

		final IStrategoTerm languagesTerm = term.getSubterm(1);
		for(IStrategoTerm languageTerm : languagesTerm)
			index.addLanguage(((IStrategoString) languageTerm).stringValue());

		return index;
	}

	private void sourceFromTerm(IIndex index, IStrategoTerm term) {
		if(!Tools.isTermTuple(term))
			throw new RuntimeException("Cannot read index: Partition term is not a tuple.");

		final IStrategoTerm source = term.getSubterm(0);
		final IStrategoTerm entryList = term.getSubterm(1);
		final Collection<IndexEntry> entries = Lists.newArrayListWithCapacity(entryList.getSubtermCount());
		for(IStrategoTerm entryTerm : term.getSubterm(1)) {
			entries.add(entryFactory.fromTerm(entryTerm));
		}
		index.addAll(source, entries);
	}
}
