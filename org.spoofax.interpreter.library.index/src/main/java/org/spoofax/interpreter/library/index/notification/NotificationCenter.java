package org.spoofax.interpreter.library.index.notification;

import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A central, static go-to point for file system notifications. Notifications are sent when files are added, deleted,
 * renamed, or modified.
 */
public class NotificationCenter {
	private static Map<ObserverDescription, INotificationService> asyncObservers =
		new HashMap<ObserverDescription, INotificationService>();

	/**
	 * @see INotificationService#notifyChanges(URI, boolean)
	 */
	public synchronized static void notifyFileChanges(URI file, boolean triggerOnSave) {
		assert file.isAbsolute();
		for(INotificationService observer : asyncObservers.values()) {
			observer.notifyChanges(file, triggerOnSave);
		}
	}

	/**
	 * @see INotificationService#notifyChanges(Iterable, boolean)
	 */
	public synchronized static void notifyFileChanges(Iterable<URI> files, boolean triggerOnSave) {
		final Iterator<URI> filesIterator = files.iterator();

		if(filesIterator.hasNext()) {
			final URI file = filesIterator.next();
			if(!filesIterator.hasNext()) {
				notifyFileChanges(file, triggerOnSave);
				return;
			}
		}

		for(URI file : files) {
			assert file.isAbsolute();
		}

		for(INotificationService observer : asyncObservers.values()) {
			observer.notifyChanges(files, triggerOnSave);
		}
	}


	/**
	 * @see INotificationService#notifyNewProjectLanguage(URI, String)
	 */
	public synchronized static void notifyNewProjectLanguage(URI projectPath, String language) {
		for(INotificationService observer : asyncObservers.values()) {
			observer.notifyNewProjectLanguage(projectPath, language);
		}
	}

	/**
	 * @see INotificationService#notifyNewProjectLanguage(URI, String)
	 */
	public synchronized static void notifyNewProject(URI projectPath) {
		for(INotificationService observer : asyncObservers.values()) {
			observer.notifyNewProject(projectPath);
		}
	}

	/**
	 * Registers an observer. Only one observer is stored at a time for a language/serviceName combination.
	 *
	 * @param language The language for this observer, may be null.
	 * @param serviceName The name of the service represented by this observer, may be null.
	 */
	public synchronized static void putObserver(String language, String serviceName, INotificationService service) {
		asyncObservers.put(new ObserverDescription(language, serviceName), service);
	}

	/**
	 * Removes an observer. Only one observer is stored at a time for a language/serviceName combination.
	 *
	 * @param language The language for this observer, may be null.
	 * @param serviceName The name of the service represented by this observer, may be null.
	 */
	public synchronized static boolean removeObserver(String language, String service) {
		return asyncObservers.remove(new ObserverDescription(language, service)) != null;
	}

	/**
	 * An observer. A wannabe case class.
	 */
	private static class ObserverDescription {
		final String language;
		final String service;

		public ObserverDescription(String language, String service) {
			this.language = language;
			this.service = service;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((language == null) ? 0 : language.hashCode());
			result = prime * result + ((service == null) ? 0 : service.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if(this == obj)
				return true;
			if(obj == null)
				return false;
			if(!(obj instanceof ObserverDescription))
				return false;
			ObserverDescription other = (ObserverDescription) obj;
			if(language == null) {
				if(other.language != null)
					return false;
			} else if(!language.equals(other.language))
				return false;
			if(service == null) {
				if(other.service != null)
					return false;
			} else if(!service.equals(other.service))
				return false;
			return true;
		}

	}
}
