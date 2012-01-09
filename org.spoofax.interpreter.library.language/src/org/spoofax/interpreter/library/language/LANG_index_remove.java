package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;
import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermString;

import java.net.URI;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_remove extends AbstractPrimitive {

	private static String NAME = "LANG_index_remove";
	
	private final SemanticIndexManager index;
	
	public LANG_index_remove(SemanticIndexManager index) {
		super(NAME, 0, 2);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if (isTermAppl(tvars[0]) && isTermString(tvars[1])) {
			IStrategoAppl template = (IStrategoAppl) tvars[0];
			URI file = index.getCurrent().toFileURI(asJavaString(tvars[1]));
			SemanticIndexEntry entry = index.getCurrent().getEntries(template);
			if (entry != null) {
				if (entry.getFile() == file)
					index.getCurrent().remove(entry);
				for (SemanticIndexEntry tailEntry : entry.getTail()) {
					if (tailEntry.getFile() == file) {
						index.getCurrent().remove(tailEntry);
					}
				}
			} 
			return true;
		} else {
			return false;
		}
	}
}
