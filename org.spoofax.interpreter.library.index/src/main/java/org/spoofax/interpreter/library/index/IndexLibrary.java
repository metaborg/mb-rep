package org.spoofax.interpreter.library.index;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

public class IndexLibrary extends AbstractStrategoOperatorRegistry {
    public static final String REGISTRY_NAME = "INDEX";

    public IndexLibrary() {
        add(new LANG_index_add());
        add(new LANG_index_remove());
        add(new LANG_index_remove_all());
        add(new LANG_index_remove_one());
        add(new LANG_index_clear_all());
        add(new LANG_index_clear_file());
        add(new LANG_index_get_all_files());
        add(new LANG_index_get_all_in_file());
        add(new LANG_index_get_children());
        add(new LANG_index_get_files_of());
        add(new LANG_index_get());
        add(new LANG_index_get_with_partitions());
        add(new LANG_index_setup());
        add(new LANG_index_commit());
        add(new LANG_index_get_files_newer_than());
        add(new LANG_index_get_current_file());
        add(new LANG_index_start_transaction());
        add(new LANG_index_end_transaction());
        add(new LANG_index_get_file_revision());
        add(new LANG_index_set_current_file());
        add(new LANG_index_reload());
        add(new LANG_index_unload());
        add(new LANG_index_start_collection());
        add(new LANG_index_stop_collection());
    }

    public String getOperatorRegistryName() {
        return REGISTRY_NAME;
    }
}
