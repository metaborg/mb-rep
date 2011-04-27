package org.spoofax.interpreter.library.language;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

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
	}

	public String getOperatorRegistryName() {
		return REGISTRY_NAME;
	}

}
