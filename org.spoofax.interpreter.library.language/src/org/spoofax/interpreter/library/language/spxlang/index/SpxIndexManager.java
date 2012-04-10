package org.spoofax.interpreter.library.language.spxlang.index;

import java.io.File;
import java.io.IOException;

import org.spoofax.interpreter.core.Tools;
import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxSymbolTableException;
import org.spoofax.interpreter.terms.IStrategoString;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

public class SpxIndexManager implements IIndexManageCommand 
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
	
	static void ensureFacadeInitialized(SpxSemanticIndexFacade f) throws SpxSymbolTableException {
		if(f== null) {
			throw new RuntimeException("Symbol Table is not initialized for project . Invoke SPX_index_init. ");
		}	
	}
	protected void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectName , Object... objects) throws Exception{} ;
	
	public void run() throws Exception{
		executeCommnad( spxSemanticIndex , projectName, arguments); 
		
	}
	
	/**
	 * @param idx
	 * @param projectPath
	 * @return
	 * @throws Exception
	 */
	public static SpxSemanticIndexFacade getSpxIndexFacade(SpxSemanticIndex idx,
			IStrategoTerm projectPath) {
		SpxSemanticIndexFacade idxFacade = null;
		try {
			idxFacade = idx.getFacadeRegistry().getFacade(projectPath);
		} catch (SpxSymbolTableException ex) {
			// do nothing
		} catch (Exception e) {
			// do nothing 
		}
		return idxFacade;
	}
	
	
	public static IIndexManageCommand getCommandInstance(final SpxSemanticIndex spxSemanticIndex , IStrategoString commandName, 
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
	
		if(commandName.equalsIgnoreCase(INVALIDATE_GLOBAL_CACHE_COMMAND_STRING))
			return invalidateGlobalCacheCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(ON_INIT_CODEGEN_COMMAND_STRING))
			return onInitCodeGenrationCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(ON_COMPLETE_CODEGEN_COMMAND_STRING))
			return onCompleteCodeGenrationCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(ON_INIT_INDEX_UPDATING_COMMAND_STRING))
			return onInitIndexUpdatingCommandInstance(spxSemanticIndex , projectPath, objects);
		
		if(commandName.equalsIgnoreCase(ON_INDEX_UPDATING_COMPLETED_COMMAND_STRING))
			return onIndexUpdatingCompletedCommandInstance(spxSemanticIndex , projectPath, objects);
		
		
		throw new IllegalArgumentException("Invalid command name :"+ commandName) ;
	}
	
	private static IIndexManageCommand onInitIndexUpdatingCommandInstance(
			final SpxSemanticIndex index, IStrategoString projectPath, Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade f = getSpxIndexFacade(idx, projectPath);
				if(f != null)
					f.onInitIndexUpdating();
			}
		};
	}
	
	private static IIndexManageCommand onIndexUpdatingCompletedCommandInstance(
			final SpxSemanticIndex index, IStrategoString projectPath, Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				
				SpxSemanticIndexFacade f = getSpxIndexFacade(idx, projectPath);
				if(f != null)
					f.onIndexUpatingCompleted();
			}
		};
	}
	
	private static IIndexManageCommand onInitCodeGenrationCommandInstance(
			final SpxSemanticIndex index, IStrategoString projectPath,
			Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade f = getSpxIndexFacade(idx, projectPath);
				if(f != null)
					f.onInitCodeGeneration();
			}
		};
	}

	private static IIndexManageCommand onCompleteCodeGenrationCommandInstance(
			final SpxSemanticIndex index, IStrategoString projectPath,
			Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				
				SpxSemanticIndexFacade idxFacade = getSpxIndexFacade(idx, projectPath);
				if(idxFacade != null)
					idxFacade.onCompleteCodeGeneration();
			}
		};
	}
	
	private static IIndexManageCommand rollbackCommandInstance(
			final SpxSemanticIndex index, IStrategoString projectPath,
			Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade f = getSpxIndexFacade(idx, projectPath);
				if(f != null)
					f.rollbackChanges();
			}
		};
	}
	
	private static  SpxIndexManager clearCommandInstance(
			SpxSemanticIndex index, 
			IStrategoString projectPath,
			Object...  objects){

		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				try { 
					SpxSemanticIndexFacade idxFacade = getSpxIndexFacade(idx, projectPath);
					if(idxFacade == null)
						idxFacade = idx.getFacadeRegistry().initFacade(projectPath, (ITermFactory)objects[0], (IOAgent)objects[1]) ;
					idxFacade.cleanIndexAndSymbolTable();
				}catch(IOException ex){
					
					try {
						if(SpxIndexUtils.deleteSpxCacheDir( new File(  projectPath +"/" + SpxIndexConfiguration.SPX_CACHE_DIRECTORY), true)){
							SpxIndexUtils.deleteSpxCacheDir( new File(  projectPath +"/" + SpxIndexConfiguration.SPX_SHADOW_DIR) , true);
						}
					}catch(Exception  ex2){
						// Ignore 	
					}
					
					throw ex;
				}
			}

			
		};
	}
	
	private static  SpxIndexManager closeCommandInstance(
			SpxSemanticIndex index, 
			IStrategoString projectPath,
			Object...  objects){

		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				
				SpxSemanticIndexFacade idxFacade = getSpxIndexFacade(idx, projectPath);
				if(idxFacade!= null){
					System.out.println("Closing Index for "+ projectPath );
					idxFacade.close(false); // Also committing if the persistence manager is still open
				}else
					System.out.println("Index already closed for "+projectPath );
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
				SpxSemanticIndexFacade f = getSpxIndexFacade(idx, projectPath);
				if(f!= null){
					f.commitChanges();
				}	
			}
		};
	}
	private static IIndexManageCommand invalidateGlobalCacheCommandInstance(
			SpxSemanticIndex index, IStrategoString projectPath,
			Object[] objects) {
		
		return new SpxIndexManager(index , projectPath, objects){
			public void executeCommnad(SpxSemanticIndex idx, IStrategoTerm projectPath, Object... objects) throws Exception{
				SpxSemanticIndexFacade idxFacade = idx.getFacade(projectPath);
				idxFacade.invalidateGlobalNamespace();
			}
		};
	}
}