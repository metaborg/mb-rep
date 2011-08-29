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
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Md. Adil Akhter
 *
 */
public class SPX_index_add_module extends AbstractPrimitive {

	private static String NAME = "SPX_index_add_module";

	private final SpxSemanticIndex index;

	public SPX_index_add_module(SpxSemanticIndex index) {
		super(NAME, 0, 1);
		this.index = index;
	}

	@Override
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) {
		if (isTermAppl(tvars[0])) {
			//IStrategoAppl entry = (IStrategoAppl) tvars[0];
			//URI file = index.toFileURI(asJavaString(tvars[1]));
			
			//index.add(entry, asJavaString(tvars[1]));
			return true;
		} else {
			return false;
		}
	}
}