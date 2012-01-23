package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_commit extends AbstractPrimitive {

	private static String NAME = "LANG_index_commit";
	
	private final SemanticIndexManager index;
	
	public LANG_index_commit(SemanticIndexManager index) {
		super(NAME, 0, 0);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		IStrategoTerm stored = index.getCurrent().toTerm();
		TermFactory simpleFactory = new TermFactory();
		stored = new TermAttachmentSerializer(simpleFactory).toAnnotations(stored);
		// TODO: store index to disk
		return true;
	}
}
