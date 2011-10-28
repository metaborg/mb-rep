package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.library.language.spxlang.index.Utils;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.IStrategoTuple;

public class SPX_index_equal_resource_uri extends SpxAbstractPrimitive {

	private static String NAME = "SPX_index_equal_resource_uri";
	private static int RESOURCE_URIS_INDEX = 1;
	private final static int NO_ARGS = 2;
	
	public SPX_index_equal_resource_uri(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateTupleTermAt(RESOURCE_URIS_INDEX);
	}

	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoTuple resTuple = (IStrategoTuple)tvars[RESOURCE_URIS_INDEX];
		IOAgent agent = SSLLibrary.instance(env).getIOAgent();
		
		String resUri1 = Tools.asJavaString(Tools.stringAt(resTuple, 0)).trim();
		String resUri2 = Tools.asJavaString(Tools.stringAt(resTuple, 1)).trim();
		
		String absPath1 =  Utils.getAbsolutePathString(resUri1, agent);
		String absPath2 =  Utils.getAbsolutePathString(resUri2, agent);
		
		return absPath1.equalsIgnoreCase(absPath2);
	}

}
