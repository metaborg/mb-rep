package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;
import static org.spoofax.interpreter.core.Tools.isTermString;

import java.net.URI;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_is_indexed_file extends AbstractPrimitive {

	private static String NAME = "LANG_index_is_indexed_file";
	
	private final SemanticIndex index;
	
	public LANG_index_is_indexed_file(SemanticIndex index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if (isTermString(tvars[0])) {
			URI file = index.toFileURI(asJavaString(tvars[0]));
			return index.isIndexed(file);
		} else {
			return false;
		}
	}
}
