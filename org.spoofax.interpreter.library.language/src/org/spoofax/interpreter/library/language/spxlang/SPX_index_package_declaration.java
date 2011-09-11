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

	private final SpxSemanticIndex index;

	public SPX_index_package_declaration(SpxSemanticIndex index) {
		super(NAME, 0, 2);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		boolean successStatement = false;
		
		if (isTermString(tvars[0]) && isTermAppl(tvars[0])) {
			
			IStrategoString projectName = (IStrategoString)tvars[0];
			IStrategoAppl packageDecl = (IStrategoAppl) tvars[1];
			
			try
			{
				successStatement = index.indexPackageDeclaration(projectName, packageDecl);
			}
			catch(Exception ex)
			{ 
				SSLLibrary.instance(env).getIOAgent().printError("["+NAME+" Invokation failed . ] Error : "+ ex.getMessage());
			}
		}
		
		return successStatement;	
	}
}