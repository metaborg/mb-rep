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
	 * Notify listener of a new language in a project.
	 *
	 * @param projectPath The project path the language was found in.
	 * @param language The name of the language.
	 */
	void notifyNewProjectLanguage(URI projectPath, String language);

	/**
	 * Notify listener of a new project.
	 *
	 * @param projectPath The project path.
	 */
	void notifyNewProject(URI projectPath);
}
