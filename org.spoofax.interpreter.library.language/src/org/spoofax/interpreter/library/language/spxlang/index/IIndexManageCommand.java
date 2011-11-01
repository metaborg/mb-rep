package org.spoofax.interpreter.library.language.spxlang.index;

public interface IIndexManageCommand {
	
	static final String CLEAR_COMMAND_STRING = "CLEAR" ;
	static final String INIT_COMMAND_STRING = "INIT" ;
	static final String CLOSE_COMMAND_STRING = "CLOSE" ;
	static final String SAVE_COMMAND_STRING = "SAVE" ;
	static final String ROLLBACK_COMMAND_STRING = "ROLLBACK" ;
	static final String INVALIDATE_GLOBAL_CACHE_COMMAND_STRING = "INVALIDATE_GLOBAL_CACHE" ;
	
	static final String INIT_CODEGEN_COMMAND_STRING = "INIT_CODEGEN" ;
	static final String SUCCESSFUL_CODEGEN_COMMAND_STRING = "SUCCESSFUL_CODEGEN" ;
	
	public void run() throws Exception;
}