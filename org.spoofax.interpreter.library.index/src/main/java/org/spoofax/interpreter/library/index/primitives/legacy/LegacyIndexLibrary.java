package org.spoofax.interpreter.library.index.primitives.legacy;

import org.spoofax.interpreter.library.index.primitives.IndexLibrary;
import org.spoofax.interpreter.library.index.primitives.LANG_index_clear_source;
import org.spoofax.interpreter.library.index.primitives.LANG_index_get_all_childs;
import org.spoofax.interpreter.library.index.primitives.LANG_index_get_all_sources;
import org.spoofax.interpreter.library.index.primitives.LANG_index_get_all_sources_of;
import org.spoofax.interpreter.library.index.primitives.LANG_index_get_all_values;
import org.spoofax.interpreter.library.index.primitives.LANG_index_get_all_values_in_source;

public class LegacyIndexLibrary extends IndexLibrary {
    public LegacyIndexLibrary() {
        super();

        add(new RedirectAbstractPrimitive("LANG_index_get", new LANG_index_get_all_values()));
        add(new RedirectAbstractPrimitive("LANG_index_clear_file", new LANG_index_clear_source()));
        add(new RedirectAbstractPrimitive("LANG_index_get_all_files", new LANG_index_get_all_sources()));
        add(new RedirectAbstractPrimitive("LANG_index_get_all_in_file", new LANG_index_get_all_values_in_source()));
        add(new RedirectAbstractPrimitive("LANG_index_get_files_of", new LANG_index_get_all_sources_of()));
        add(new RedirectAbstractPrimitive("LANG_index_clear_partition", new LANG_index_clear_source()));
        add(new RedirectAbstractPrimitive("LANG_index_get_all_in_partition", new LANG_index_get_all_values_in_source()));
        add(new RedirectAbstractPrimitive("LANG_index_all_partitions", new LANG_index_get_all_sources()));
        add(new RedirectAbstractPrimitive("LANG_index_get_partitions_of", new LANG_index_get_all_sources_of()));
        add(new RedirectAbstractPrimitive("LANG_index_get_children", new LANG_index_get_all_childs()));

        add(new RemovedAbstractPrimitive("LANG_index_clear_all", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_commit", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_end_transaction", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_get_current_file", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_get_current_partition", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_get_file_revision", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_get_files_newer_than", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_get_with_partitions", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_merge", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_persist", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_pop", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_push", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_recover", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_reload", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_remove", 0, 2));
        add(new RemovedAbstractPrimitive("LANG_index_remove_all", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_remove_one", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_reset", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_set_current_file", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_set_current_partition", 0, 1));
        add(new RemovedAbstractPrimitive("LANG_index_setup", 0, 3));
        add(new RemovedAbstractPrimitive("LANG_index_setup_new", 0, 2));
        add(new RemovedAbstractPrimitive("LANG_index_start_transaction", 0, 0));
        add(new RemovedAbstractPrimitive("LANG_index_unload", 0, 1));
    }
}
