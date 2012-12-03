package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

/**
 * @author Gabriël Konat
 */
public class IndexLibrary extends AbstractStrategoOperatorRegistry {
    public static final String REGISTRY_NAME = "INDEX";

    public IndexLibrary() {
        IndexManager index = new IndexManager();
        add(new LANG_index_add(index));
        add(new LANG_index_remove(index));
        add(new LANG_index_clear_all(index));
        add(new LANG_index_clear_file(index));
        add(new LANG_index_get_all_files(index));
        add(new LANG_index_get_all_in_file(index));
        add(new LANG_index_get_children(index));
        add(new LANG_index_get_files_of(index));
        add(new LANG_index_get(index));
        add(new LANG_index_setup(index));
        add(new LANG_index_commit(index));
        add(new LANG_index_get_files_newer_than(index));
        add(new LANG_index_get_current_file(index));
        add(new LANG_index_start_transaction(index));
        add(new LANG_index_end_transaction(index));
        add(new LANG_index_get_file_revision(index));
        add(new LANG_index_set_current_file(index));
        add(new LANG_index_reload(index));
    }

    public String getOperatorRegistryName() {
        return REGISTRY_NAME;
    }
}
