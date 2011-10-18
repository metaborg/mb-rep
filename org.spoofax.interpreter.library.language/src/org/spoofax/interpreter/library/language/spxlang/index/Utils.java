package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.spxlang.index.data.IdentifiableConstruct;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

public final class Utils {
	private Utils() {
		
	}
	public static final int NO_OF_ATTEMPT_TO_INIT_RECORDMANAGER = 1;
	
	public static final String All = "*";
	public static final String All_SYMBOLS = "\"*\"";
	public static final String CURRENT = ".";
	public static final String SPX_CACHE_DIRECTORY = ".spxcache";
	public static final String SPX_INDEX_DIRECTORY = ".spxindex";
	
	static final boolean DEBUG = false;
	
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
	
	static String now(String dateFormat) {
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
	    return sdf.format(cal.getTime());
	}
	
	// Deletes all the file and directories exists inside 
	// SPX Index directory. If it can't delete the files 
	// then just ignore it. 
	static  void  tryDeleteSpxIndexDir(File cacheDir){
	    if ( cacheDir.exists() &&  cacheDir.isDirectory()) {
	        String[] children = cacheDir.list();
	        for (int i=0; i<children.length; i++) {
	           deleteSpxCacheDir(new File(cacheDir, children[i]));
	        }
	    }
	    cacheDir.delete();
	}
	
	// Deletes all the files and directories inside 
	// the cache directory denoted using cacheDir. 
	// If it fails to delete any Files or Directories
	// , it returns false. Otherwise, it returns true.
	static  boolean deleteSpxCacheDir(File cacheDir){
	    if ( cacheDir.exists() &&  cacheDir.isDirectory()) {
	        String[] children = cacheDir.list();
	        for (int i=0; i<children.length; i++) {
	            boolean success = deleteSpxCacheDir(new File(cacheDir, children[i]));
	            if (!success) {
	                return false;
	            }
	        }
	    }
	    return cacheDir.delete();
	}
	
	public static String serializeToString(TermAttachmentSerializer serializer , IStrategoTerm t) throws IOException{
		IStrategoTerm annotatedTerm = serializer.toAnnotations(t);
		
		StringBuilder sb = new StringBuilder();
		annotatedTerm.writeAsString(sb ,Integer.MAX_VALUE);
		
		return sb.toString();
	}
	
	public static IStrategoTerm deserializeToTerm(ITermFactory fac , TermAttachmentSerializer serializer, String termString){
		IStrategoTerm deserializedAtermWithAnnotation = fac.parseFromString(termString);
		IStrategoTerm deserializedAterm  = serializer.fromAnnotations(deserializedAtermWithAnnotation, true);
		
		return deserializedAterm;
	}
}
