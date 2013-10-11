package org.spoofax.interpreter.library.index.notification;

import java.net.URI;

public interface INotificationService {
    /**
	 * Notify listener of an added/removed/changed file.
	 *
	 * @param file The URI of the file
	 * @param triggerOnSave If the on save handler of changed file should be called.
	 */
	void notifyChanges(URI file, boolean triggerOnSave);

    /**
	 * Notify listeners of multiple added/removed/changed files.
	 *
	 * @param files The changed files.
	 * @param triggerOnSave If the on save handler of changed file should be called.
	 */
	void notifyChanges(Iterable<URI> files, boolean triggerOnSave);

    /**
     * Notify listener of a new project.
     *
     * @param project The new project.
     */
    void notifyNewProject(URI project);
}
