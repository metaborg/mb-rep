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
public class SPX_index_package_declaration extends AbstractPrimitive {

	private static String NAME = "SPX_index_package_declaration";
	
	private final static int PROJECT_NAME_INDEX = 0;
	private final static int PACKAGE_DECL_INDEX = 1;
	
	private final static int NO_ARGS = 2;
	
	private final SpxSemanticIndex index;
	
	public SPX_index_package_declaration(SpxSemanticIndex index) {
		super(NAME, 0, NO_ARGS);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		boolean successStatement = false;
		
		if ((NO_ARGS == tvars.length) && isTermString(tvars[PROJECT_NAME_INDEX]) && isTermAppl(tvars[PACKAGE_DECL_INDEX])) {
			
			IStrategoString projectName = (IStrategoString)tvars[PROJECT_NAME_INDEX];
			IStrategoAppl packageDecl   = (IStrategoAppl) tvars[PACKAGE_DECL_INDEX];
			
			try
			{
				successStatement = index.indexPackageDeclaration(projectName, packageDecl);
			}
			catch(Exception ex)
			{ 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error : "+ ex.getMessage());
			}
		}
		else
			SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error :  Mismatch in provided arguments. Variables provided : "+ tvars);
		
		return successStatement;	
	}
}