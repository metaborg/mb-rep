package org.spoofax.interpreter.library.language.spxlang;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.language.spxlang.SpxAbstractPrimitive.SpxPrimitiveValidator;
import org.spoofax.interpreter.library.language.spxlang.index.IIndexManageCommand;
import org.spoofax.interpreter.library.language.spxlang.index.SpxIndexManager;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SPX_exec_index_manage_command extends SpxAbstractPrimitive {
	private static String NAME = "SPX_exec_index_manage_command";
	private final static int NO_ARGS = 2;
	private static int COMMAND_STRING_INDEX = 1;
	
	public SPX_exec_index_manage_command(SpxSemanticIndex index) {
		super(index, NAME, 0, NO_ARGS);
	}
	
	@Override
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return super.validateArguments(env, svars, tvars)
					.validateStringTermAt(COMMAND_STRING_INDEX);
	}
	
	@Override
	protected boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception {
		IStrategoString commandString = (IStrategoString)tvars[COMMAND_STRING_INDEX];
		
		IIndexManageCommand command = 
			SpxIndexManager.getCommandInstance(index, commandString, getProjectPath(tvars),env.getFactory(), SSLLibrary.instance(env).getIOAgent());

		command.run();
		return true;
	}
}
