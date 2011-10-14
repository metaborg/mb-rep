package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;
import org.spoofax.interpreter.library.language.spxlang.*;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class LanguageLibrary extends AbstractStrategoOperatorRegistry {
	
	public static final String REGISTRY_NAME = "LANGUAGE";

	public LanguageLibrary() {
		SemanticIndex index = new SemanticIndex();
		add(new LANG_index_add(index));
		add(new LANG_index_clear_all(index));
		add(new LANG_index_clear_file(index));
		add(new LANG_index_get_all_files(index));
		add(new LANG_index_get_children(index));
		add(new LANG_index_get_descendants(index));
		add(new LANG_index_get_files_of(index));
		add(new LANG_index_get(index));
		add(new LANG_index_is_indexed_file(index));
		add(new LANG_index_setup(index));
		add(new LANG_get_all_projects_in_Workspace());
		
		SpxSemanticIndex spxIndex= new SpxSemanticIndex();
		
		//TODO : generalize and reduce/refactor primitives
		//primitives for index initialization and management
		//add(new SPX_index_init(spxIndex));
		add(new SPX_exec_index_manage_command(spxIndex));
		//add(new SPX_index_save(spxIndex));
		//add(new SPX_index_clear(spxIndex));
		
		//primitives to index spoofaxlang compilation unit 
		add(new SPX_index_compilation_unit(spxIndex));
		add(new SPX_index_get_compilation_unit(spxIndex));
		
		//primitives to index packages, language descriptions  and modules 
		add(new SPX_index_package_declaration(spxIndex));
		add(new SPX_index_language_descriptor(spxIndex));
		add(new SPX_index_module_definition(spxIndex));
		
		add(new SPX_index_get_language_descriptor(spxIndex));
		add(new SPX_index_get_package_declaration(spxIndex));
		add(new SPX_index_get_module_declaration(spxIndex));
		add(new SPX_index_get_module_definition(spxIndex));
		
		add(new SPX_index_get_module_declarations_of(spxIndex));
		add(new SPX_index_get_package_declarations_of(spxIndex));
		add(new SPX_index_import_references(spxIndex));
		add(new SPX_index_get_imports(spxIndex));
		
		//Primitives related to symbol-table - i.e. symbol definition and resolving
		add(new SPX_symtab_new_scope(spxIndex));
		add(new SPX_symtab_destroy_scope(spxIndex));
		add(new SPX_symtab_define_symbol(spxIndex));
		add(new SPX_symtab_resolve_symbols(spxIndex));
		
	}

	public String getOperatorRegistryName() {
		return REGISTRY_NAME;
	}

}
