package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.core.IContext;
import org.spoofax.interpreter.core.InterpreterException;
import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.AbstractPrimitive;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndex;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacade;
import org.spoofax.interpreter.library.language.spxlang.index.SpxSemanticIndexFacadeRegistry;
import org.spoofax.interpreter.library.ssl.SSLLibrary;
import org.spoofax.interpreter.stratego.Strategy;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;

public abstract class SpxAbstractPrimitive extends AbstractPrimitive{
	
	private final static int PROJECT_PATH_INDEX = 0;

	protected final SpxSemanticIndex index;
	
	public SpxAbstractPrimitive(SpxSemanticIndex index, String name, int svars, int tvars ) {
		super(name, svars, tvars);
		
		this.index = index;
	}
	
	protected IStrategoString getProjectPath( IStrategoTerm[] tvars){
		//TODO : get project path from EditorIOAgent instead of passing as an argument
		return (IStrategoString)tvars[PROJECT_PATH_INDEX]; 
	}
	
	protected SpxPrimitiveValidator validateArguments(IContext env, Strategy[] svars, IStrategoTerm[] tvars){
		return SpxPrimitiveValidator.newValidator()
					.validatePrimitive(getName()).with(env, tvars)								
												 .validateArity(getTArity())
												 .validateStringTermAt(PROJECT_PATH_INDEX);
	}
	
	protected abstract boolean executePrimitive(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws Exception;
	
	@Override 
	public boolean call(IContext env, Strategy[] svars, IStrategoTerm[] tvars) throws InterpreterException {
		boolean successStatement = false;
		IOAgent agent = SSLLibrary.instance(env).getIOAgent();
		try {
			validateArguments(env, svars, tvars);
			successStatement = executePrimitive(env, svars, tvars) ;
		}
		catch (Exception ex) {
			if( ex instanceof IOException ||  ex instanceof IllegalStateException){
				logException(agent , ex);
				tryCleanupResources( index.getFacadeRegistry() ,  getProjectPath(tvars) , agent);
			}
		}
		catch (Error e) {
			logException(agent , e);
			tryCleanupResources( index.getFacadeRegistry() ,  getProjectPath(tvars) , agent);
			throw e;
		}
		return successStatement; 
	};
	
	void tryCleanupResources(SpxSemanticIndexFacadeRegistry registry,IStrategoTerm projectPath , IOAgent agent){

		if (registry.containsFacade(projectPath)) {
			SpxSemanticIndexFacade facade =null; 
			try {
				facade = registry.getFacade(projectPath);
				if(facade!=null){
					facade.clearCache();
					facade.close();
				}
			} catch (Exception e) {
				logMessage( agent, "Cleanup Failed due to error :"+ e.getMessage() );
			} catch (Error e) {
				logMessage( agent, "Cleanup Failed due to error :"+ e.getMessage() );
			}
		}
	}
	
	void logMessage(IOAgent agent , String message){ 
		agent.printError("[" + this.getName() + "] " + message);
	}
		
	void logException(IOAgent agent , Throwable ex){
		agent.printError("[" + this.getName() + "]  Invocation failed . "
									+ ex.getClass().getSimpleName()
									+ " | error message: " + ex.getMessage());
	}
	
	static class SpxPrimitiveValidator{
		IContext env;
		IStrategoTerm[] tvars;
		String primitiveName;

		static SpxPrimitiveValidator newValidator() {
			return new SpxPrimitiveValidator();
		}
		private  SpxPrimitiveValidator(){}

		SpxPrimitiveValidator validatePrimitive( String primitiveName){
			this.primitiveName = primitiveName;
			return this;
		}

		SpxPrimitiveValidator with(IContext env, IStrategoTerm[] tvars) { 
			this.env = env;
			this.tvars = tvars;

			return this;
		}

		SpxPrimitiveValidator validateArity(int expectedArity){
			if (expectedArity != tvars.length){
				throwException("Mismatch in argument arity."
						+" Expected "+ expectedArity
						+ "actual" +  tvars.length);	
			}
			return this;
		}

		SpxPrimitiveValidator validateStringTermAt(int ordinal){
			if(!Tools.isTermString(tvars[ordinal])){
				throwException("Illegal Arugments. Expected IStrategoString"
						+" [TermString] "
						+"at ordinal "+ ordinal
						+"actual" +  tvars[ordinal]);
			}
			return this;
		}

		SpxPrimitiveValidator validateListTermAt(int ordinal){
			if(!Tools.isTermList(tvars[ordinal])){
				throwException( "Illegal Arugments. Expected "
						+" [TermList] "
						+"at ordinal : "+ ordinal
						+"actual : " +  tvars[ordinal]);
			}
			return this;
		}
		
		SpxPrimitiveValidator validateApplTermAt(int ordinal){
			if(!Tools.isTermAppl(tvars[ordinal])){
				throwException( "Illegal Arugments. Expected "
						+" [TermAppl] "
						+"at ordinal : "+ ordinal
						+"actual : " +  tvars[ordinal]);
			}
			return this;
		}
		SpxPrimitiveValidator validateTupleTermAt(int ordinal) {
			if(!Tools.isTermTuple(tvars[ordinal])){
				throwException( "Illegal Arugments. Expected "
						+" [TermTuple] "
						+"at ordinal : "+ ordinal
						+"actual : " +  tvars[ordinal]);
			}
			return this;
		} 
		
		
		void throwException(String errorMessage){
			SSLLibrary
				.instance(env)
				.getIOAgent()
				.printError("["+this.primitiveName +"] " +
						"Error Occured during Argument Validation : " +
						"" + errorMessage);

			throw new IllegalArgumentException( errorMessage );
		}
		
	}
}
