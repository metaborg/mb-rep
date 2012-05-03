package org.spoofax.interpreter.library.language;

import java.util.Collection;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class LANG_index_get_all_files extends AbstractPrimitive {

	private static String NAME = "LANG_index_all_files";
	
	private final SemanticIndexManager index;
	
	public LANG_index_get_all_files(SemanticIndexManager index) {
		super(NAME, 0, 0);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		ITermFactory factory = env.getFactory();
		IStrategoList results = getAllFiles(index.getCurrent(), factory);
		System.out.println(results.toString());
		env.setCurrent(results);
		return true;
	}

	public static IStrategoList getAllFiles(ISemanticIndex index, ITermFactory factory) {
		Collection<SemanticIndexFileDescriptor> allFileDescriptors = index.getAllFileDescriptors();
		IStrategoList results = factory.makeList();
		for (SemanticIndexFileDescriptor fileDescriptor : allFileDescriptors) {
			results = factory.makeListCons(fileDescriptor.toTerm(factory), results);
		}
		return results;
	}
}
