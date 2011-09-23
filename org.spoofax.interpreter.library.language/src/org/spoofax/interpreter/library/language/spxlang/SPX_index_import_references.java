/**
 * 
 */
package org.spoofax.interpreter.library.language.spxlang;

import static org.spoofax.interpreter.core.Tools.asJavaString;
import static org.spoofax.interpreter.core.Tools.isTermAppl;
import static org.spoofax.interpreter.core.Tools.isTermString;

import java.net.URI;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 *
 */
public class SPX_index_import_references extends AbstractPrimitive {

	private final static String NAME = "SPX_index_import_references";
	
	private final static int PROJECT_NAME_INDEX    = 0;
	private final static int IMPORT_REFERENCES_INDEX = 1;
	
	private final static int NO_ARGS = 3;
	
	private final SpxSemanticIndex index;
	
	public SPX_index_import_references(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		boolean successStatement = false;
		
		if ((NO_ARGS == tvars.length) && isTermString(tvars[PROJECT_NAME_INDEX]) && isTermAppl(tvars[IMPORT_REFERENCES_INDEX])) {
			
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl importReferences   = (IStrategoAppl) tvars[IMPORT_REFERENCES_INDEX];
			
			try
			{
				successStatement = index.indexImportReferences(projectName, importReferences);
			}
			catch(Exception ex)
			{ 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed . "+ ex.getClass().getSimpleName() +" | error message: " + ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+"]  Invocation failed .  Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
		
		return successStatement;	
	}
}