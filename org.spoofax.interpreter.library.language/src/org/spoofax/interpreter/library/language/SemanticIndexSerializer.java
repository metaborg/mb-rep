package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexSerializer {
	
	private final ITermFactory factory;

	public SemanticIndexSerializer(ITermFactory factory) {
		this.factory = factory;
	}
	
	public void write(Appendable output) {
		// TODO: write as one big term with position info in annos or something
		// 1 use SemanticIndexEntry.toTerms()
		// 2 use TermAttachmentSerializer
		// 3 ???
		// 4 profit!!
	}
}
