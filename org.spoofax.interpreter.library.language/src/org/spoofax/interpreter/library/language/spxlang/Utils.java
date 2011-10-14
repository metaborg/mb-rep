package org.spoofax.interpreter.library.language.spxlang;

import java.io.File;
import java.net.URI;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.ITermFactory;

public final class Utils {
	private Utils() {
		
	}
	static final String All     = "*";
	static final String CURRENT = ".";
	
	static final boolean DEBUG = true;
	
	/**
	 * Constructs {@link IStrategoList} from {@code decls}  
	 * 
	 * @param idxFacade an instance of {@link SpxSemanticIndexFacade }
	 * @param decls A collection of ModuleDeclataions 
	 * @return {@link IStrategoList}
	 */
	public static <T extends IdentifiableConstruct> IStrategoList toTerm( SpxSemanticIndexFacade idxFacade , Iterable<T> decls){
		ITermFactory termFactory = idxFacade.getTermFactory();
		IStrategoList result = termFactory.makeList();
		
		if(decls!=null){	
			for ( T decl: decls)
				result = termFactory.makeListCons(decl.toTerm(idxFacade), result);
		}
		
		return result;
	}

	public static String uriToAbsPathString(URI uri){
		return new File(uri).getAbsolutePath();
	}
	
	public static URI getAbsolutePathUri(String path, IOAgent agent){
		File file = new File(path);
		
		return	file.isAbsolute()? file.toURI() : new File(agent.getWorkingDir(), path).toURI();
	}
	
	public static String getAbsolutePathString(String path , IOAgent agent){
		return uriToAbsPathString(getAbsolutePathUri(path , agent));
	}

	public static String toAbsPathString(String path) {
		return new File(path).getAbsolutePath();
	}
	
}
