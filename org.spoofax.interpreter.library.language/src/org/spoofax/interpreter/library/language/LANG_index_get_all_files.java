package org.spoofax.interpreter.library.language;

import java.io.File;
import java.net.URI;
import java.util.Set;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_get_all_files extends AbstractPrimitive {

	private static String NAME = "LANG_index_all_files";
	
	private final SemanticIndexManager index;
	
	public LANG_index_get_all_files(SemanticIndexManager index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		Set<URI> allFiles = index.getCurrent().getAllFiles();
		ITermFactory factory = env.getFactory();
		IStrategoList results = factory.makeList();
		for (URI file : allFiles) {
			IStrategoString result = factory.makeString(new File(file).getAbsolutePath());
			results = factory.makeListCons(result, results);
		}
		env.setCurrent(results);
		return true;
	}
}
