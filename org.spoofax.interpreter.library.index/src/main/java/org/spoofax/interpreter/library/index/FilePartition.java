package org.spoofax.interpreter.library.index;

import java.net.URI;

/**
 * Container for file URI and partition.
 */
public class FilePartition {
    public final URI file;
    public final String partition;

    public FilePartition(URI file, String partition) {
        this.file = file;
        this.partition = partition;
    }
}
