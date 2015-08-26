package org.spoofax.interpreter.library.index.primitives;

import org.spoofax.interpreter.library.AbstractStrategoOperatorRegistry;

public class IndexLibrary extends AbstractStrategoOperatorRegistry {
    public static final String REGISTRY_NAME = "INDEX";

    public IndexLibrary() {
        add(new LANG_index_add());
        add(new LANG_index_add_boolean());
        add(new LANG_index_clear_source());
        add(new LANG_index_collect());
        add(new LANG_index_collect_boolean());
        add(new LANG_index_get_all_childs());
        add(new LANG_index_get_all_pairs());
        add(new LANG_index_get_all_pairs_in_source());
        add(new LANG_index_get_all_sources());
        add(new LANG_index_get_all_sources_of());
        add(new LANG_index_get_all_values());
        add(new LANG_index_get_all_values_in_source());
        add(new LANG_index_get_one_pair());
        add(new LANG_index_get_one_value());
        add(new LANG_index_start_collection());
        add(new LANG_index_stop_collection());
    }

    @Override public String getOperatorRegistryName() {
        return REGISTRY_NAME;
    }
}
