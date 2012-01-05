package org.spoofax.interpreter.library.language;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexManager {

	private SemanticIndex current;
	
	// (don't have access to WeakValueHashMap here, this is close enough)
	private static Map<String, Map<URI, WeakReference<SemanticIndex>>> indicesByLanguage =
		new HashMap<String, Map<URI, WeakReference<SemanticIndex>>>();
	
	public SemanticIndex getCurrent() {
		return current;
	}
	
	public void loadIndex(String language, URI projectPath) {
		Map<URI, WeakReference<SemanticIndex>> indicesByProject =
			indicesByLanguage.get(language);
		if (indicesByProject == null) {
			indicesByProject = new HashMap<URI, WeakReference<SemanticIndex>>();
			indicesByLanguage.put(language, indicesByProject);
		}
		WeakReference<SemanticIndex> indexRef = indicesByProject.get(projectPath);
		SemanticIndex index = indexRef == null ? null : indexRef.get();
		if (index == null) {
			index = new SemanticIndex();
			indicesByProject.put(projectPath, new WeakReference<SemanticIndex>(index));
		}
		this.current = index;
	}
}
