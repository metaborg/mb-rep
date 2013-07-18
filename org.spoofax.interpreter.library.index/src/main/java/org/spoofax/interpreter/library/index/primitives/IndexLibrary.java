package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

public class IndexLibrary extends AbstractStrategoOperatorRegistry {
    public static final String REGISTRY_NAME = "INDEX";

    public IndexLibrary() {
        add(new LANG_index_add());
        
        add(new LANG_index_reset());
        add(new LANG_index_clear_partition());
        
        add(new LANG_index_get_all_partitions());
        add(new LANG_index_get_all_in_partition());
        add(new LANG_index_get_children());
        add(new LANG_index_get_partitions_of());
        add(new LANG_index_get());
        
        add(new LANG_index_setup());
        add(new LANG_index_persist());
        
        add(new LANG_index_recover());
        add(new LANG_index_reload());
        add(new LANG_index_unload());
        
        add(new LANG_index_start_collection());
        add(new LANG_index_stop_collection());
        
        add(new LANG_index_push());
        add(new LANG_index_pop());
        add(new LANG_index_merge());
    }

    public String getOperatorRegistryName() {
        return REGISTRY_NAME;
    }
}
