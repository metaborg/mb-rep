package org.spoofax.interpreter.library.language.spxlang.index;

public interface IIndexManageCommand {
	
	static final String CLEAR_COMMAND_STRING = "CLEAR" ;
	static final String INIT_COMMAND_STRING = "INIT" ;
	static final String CLOSE_COMMAND_STRING = "CLOSE" ;
	static final String SAVE_COMMAND_STRING = "SAVE" ;
	static final String ROLLBACK_COMMAND_STRING = "ROLLBACK" ;
	static final String INVALIDATE_GLOBAL_CACHE_COMMAND_STRING = "INVALIDATE_GLOBAL_CACHE" ;
	
	static final String ON_INIT_CODEGEN_COMMAND_STRING = "ON_INIT_CODEGEN" ;
	static final String ON_COMPLETE_CODEGEN_COMMAND_STRING = "ON_COMPLETE_CODEGEN" ;
	
	static final String ON_INIT_INDEX_UPDATING_COMMAND_STRING = "ON_INIT_INDEX_UPDATING";
	static final String ON_INDEX_UPDATING_COMPLETED_COMMAND_STRING = "ON_INDEX_UPDATING_COMPLETED";
	
	public void run() throws Exception;
}