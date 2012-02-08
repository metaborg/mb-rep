package org.spoofax.interpreter.library.language;

import java.net.URI;

import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public interface INotificationService {
	
	void notifyFileChanges(URI file, String subfile);

	void notifyNewProject(URI project);
}
