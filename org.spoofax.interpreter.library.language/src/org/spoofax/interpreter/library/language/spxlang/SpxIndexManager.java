package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

interface IIndexManageCommand {
	
	static final String CLEAR_COMMAND_STRING = "CLEAR" ;
	static final String INIT_COMMAND_STRING = "INIT" ;
	static final String CLOSE_COMMAND_STRING = "CLOSE" ;
	static final String SAVE_COMMAND_STRING = "SAVE" ;
	static final String ROLLBACK_COMMAND_STRING = "ROLLBACK" ;
	
	public void run() throws Exception;
}

class SpxIndexManager implements IIndexManageCommand 
{
	private final SpxSemanticIndex spxSemanticIndex;
	private final IStrategoTerm projectName; 
	private final Object[] arguments;
	/**
	 * @param spxSemanticIndex
	 */
	SpxIndexManager(SpxSemanticIndex spxSemanticIndex , IStrategoTerm projectPath , Object... args) {
		this.spxSemanticIndex = spxSemanticIndex;
		this.arguments = args;
		this.projectName = projectPath;
	}
	
	protected void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectName , Object... objects) throws Exception{} ;
	
	public void run() throws Exception{
		executeCommnad( spxSemanticIndex , projectName, arguments); 
		
	}
	

	static IIndexManageCommand getCommandInstance(final SpxSemanticIndex spxSemanticIndex , IStrategoString commandName, 
			IStrategoString projectName, Object... objects){
		
		return getCommandInstance(spxSemanticIndex ,Tools.asJavaString(commandName) , projectName, objects);
	}

	static IIndexManageCommand getCommandInstance(
			final SpxSemanticIndex spxSemanticIndex, 
			String commandName, 
			IStrategoString projectPath,
			Object... objects) {
		
		if(commandName.equalsIgnoreCase(CLEAR_COMMAND_STRING))
			return clearCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(SAVE_COMMAND_STRING))
			return saveCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(CLOSE_COMMAND_STRING))
			return closeCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(INIT_COMMAND_STRING))
			return initCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(ROLLBACK_COMMAND_STRING))
			return rollbackCommandInstance(spxSemanticIndex , projectPath, objects);
		
		throw new IllegalArgumentException("Invalid Command Name : "+ commandName) ;
	}
	private static IIndexManageCommand rollbackCommandInstance(
			final SpxSemanticIndex index, IStrategoString projectPath,
			Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade idxFacade = idx.getFacade(projectPath);
				idxFacade.rollbackChanges();
			}
		};
	}
	private static  SpxIndexManager clearCommandInstance(
			SpxSemanticIndex index, 
			IStrategoString projectPath,
			Object...  objects){

		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade idxFacade = idx.getFacade(projectPath);
				idxFacade.reinitSymbolTable();
			}
		};
	}
	
	private static  SpxIndexManager closeCommandInstance(
			SpxSemanticIndex index, 
			IStrategoString projectPath,
			Object...  objects){

		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade idxFacade =  idx.getFacade(projectPath);
				idxFacade.persistChanges();
				idxFacade.close();
			}
		};
	}
	
	private static  SpxIndexManager initCommandInstance(
			SpxSemanticIndex index, 
			IStrategoString projectPath,
			Object...  objects){

		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				idx.getFacadeRegistry().initFacade(projectPath, 
													(ITermFactory)objects[0], 
													(IOAgent)objects[1]) ;
			}
		};
	}
	/**
	 * Creates command that saved the indexes of the project specified by the projectName
	 * 
	 * @param projectPath Term representation of the projectName 
	 * @return true if the operation is successful ; otherwise false.
	 * @throws IOException 
	 * @throws SpxSymbolTableException 
	 */
	private static  SpxIndexManager saveCommandInstance(
			SpxSemanticIndex index, 
			IStrategoString projectPath,
			Object...  objects){

		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade idxFacade = idx.getFacade(projectPath);
				idxFacade.persistChanges();
			}
		};
	}
	
}