package org.spoofax.interpreter.library.language;

import java.net.URI;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface INotificationService {
	
	/**
	 * Notify listener of a added/removed/changed file.
	 */
	void notifyFileChanges(URI file, String subfile);
	
	/**
	 * Notify listeners of multiple added/removed/changed files.
	 */
	void notifyFileChanges(FileSubfile[] files);

	/**
	 * Notify listener of a new project.
	 * All files in it should be compared to the
	 * timestamps or other metadata stored about them.
	 */
	void notifyNewProject(URI project);
	
	/**
	 * Container for file URI and subfile name.
	 */
	public class FileSubfile {
		public FileSubfile(URI file, String subfile) {
			this.file = file;
			this.subfile = subfile;
		}
		
		public URI file;
		public String subfile;
	}
}
