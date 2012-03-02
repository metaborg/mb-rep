package org.spoofax.interpreter.library.language;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SemanticIndexStatistics {
	private class Data {
		public Collection<SemanticIndexFile> files;
		public Collection<SemanticIndexEntry> entries;
		public Map<IStrategoConstructor, Integer> entriesPerConstructor;
		public Map<IStrategoTerm, Integer> entriesPerNamespace;
	}
	
	private SemanticIndex index;
	
	public SemanticIndexStatistics(SemanticIndex index) {
		this.index = index;
	}
	
	private Collection<SemanticIndexFile> files() {
		return index.getAllFiles();
	}
	
	private Collection<SemanticIndexEntry> elements() {
		List<SemanticIndexEntry> entries = new ArrayList<SemanticIndexEntry>();
		for(SemanticIndexFile file : index.getAllFiles()) {
			entries.addAll(file.getEntries());
		}
		
		return entries;
	}
	
	public Data stats() {
		Data data = new Data();
		data.files = files();
		data.entries = elements();
		
		// Divide types by constructor and namespace
		data.entriesPerConstructor = new HashMap<IStrategoConstructor, Integer>();
		data.entriesPerNamespace = new HashMap<IStrategoTerm, Integer>();
		for(SemanticIndexEntry entry : data.entries) {
			Integer constructorCount = data.entriesPerConstructor.get(entry.getConstructor());
			if(constructorCount == null)
				data.entriesPerConstructor.put(entry.getConstructor(), 1);
			else
				data.entriesPerConstructor.put(entry.getConstructor(), constructorCount + 1);
				
			Integer namespaceCount = data.entriesPerNamespace.get(entry.getNamespace());
			if(namespaceCount == null)
				data.entriesPerNamespace.put(entry.getNamespace(), 1);
			else
				data.entriesPerNamespace.put(entry.getNamespace(), namespaceCount + 1);
		}
		
		return data;
	}
	
	public String statsString() {
		Data data = stats();
		
		StringBuilder b = new StringBuilder();
		b.append("* Number of files: " + data.files.size() + "\n");
		b.append("* Number of elements: " + data.entries.size() + "\n");
		b.append("* Elements per constructor: \n");
		for(Entry<IStrategoConstructor, Integer> pair : data.entriesPerConstructor.entrySet())
			b.append("  - " + pair.getKey() + ": " + pair.getValue() + "\n");
		b.append("* Elements per namespace: \n");
		for(Entry<IStrategoTerm, Integer> pair : data.entriesPerNamespace.entrySet())
			b.append("  - " + pair.getKey() + ": " + pair.getValue() + "\n");
		
		return b.toString();
	}
	
	public void printStats() {
		System.out.println(statsString());
	}
}
