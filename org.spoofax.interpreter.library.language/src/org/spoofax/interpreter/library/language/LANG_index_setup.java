package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.asJavaString;

import java.net.URI;

import org.spoofax.NotImplementedException;
import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LANG_index_setup extends AbstractPrimitive {

	private static String NAME = "LANG_index_setup";
	
	private final SemanticIndexManager index;
	
	public LANG_index_setup(SemanticIndexManager index) {
		super(NAME, 0, 2);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		IStrategoString language = (IStrategoString) tvars[0];
		IStrategoList projectPaths = (IStrategoList) tvars[1];
		if (projectPaths.size() != 1) {
			throw new NotImplementedException("Multiple project paths");
		}
		IOAgent agent = SSLLibrary.instance(env).getIOAgent();
		URI projectPathURI = SemanticIndex.toFileURI(asJavaString(projectPaths.head()), agent);
		index.loadIndex(asJavaString(language), projectPathURI); 
		index.getCurrent().initialize(env.getFactory(), agent);
		return true;
	}
}
