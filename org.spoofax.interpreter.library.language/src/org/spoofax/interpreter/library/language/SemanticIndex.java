package org.spoofax.interpreter.library.language;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndex {
	
	private final Map<SemanticIndexEntry, SemanticIndexEntry> table =
		new HashMap<SemanticIndexEntry, SemanticIndexEntry>();
	
	private final Map<URI, Set<SemanticIndexEntry>> fileTable =
		new HashMap<URI, Set<SemanticIndexEntry>>();

	private IOAgent agent;

	private ITermFactory termFactory;
	
	private SemanticIndexEntryFactory factory;
	
	private SemanticIndexEntry entryTemplate;
	
	public void initialize(ITermFactory factory, IOAgent agent) {
		this.agent = agent;
		this.factory = new SemanticIndexEntryFactory(factory);
		this.termFactory = factory;
		entryTemplate = new SemanticIndexEntry(
			factory.makeConstructor("template", 0), factory.makeList(), factory.makeList(), null, null);
	}
	
	public void ensureInitialized() {
		if (factory == null)
			throw new IllegalStateException("Semantic index not initialized");
	}
	
	public SemanticIndexEntryFactory getFactory() {
		return factory;
	}
	
	public void add(IStrategoAppl entry, URI file) {
		ensureInitialized();
		IStrategoTerm type = factory.getEntryType(entry);
		IStrategoList id = factory.getEntryId(entry);
		IStrategoTerm namespace = factory.getEntryNamespace(entry);
		IStrategoTerm data = factory.getEntryData(entry);
		SemanticIndexEntryParent parent = getEntryParentAbove(namespace, id, true);
		add(factory.createEntry(type, namespace, id, data, parent, file), parent);
	}
	
	public void add(SemanticIndexEntry entry) {
		ensureInitialized();
		add(entry, getEntryParentAbove(entry.getNamespace(), entry.getId(), true));
	}
	
	private void add(SemanticIndexEntry entry, SemanticIndexEntryParent parent) {
		if (parent != null)
			parent.add(entry);
		SemanticIndexEntry existing = table.get(entry);
		if (existing == null) {
			table.put(entry, entry);
			if (entry.getFile() != null)
				getFileSet(entry.getFile()).add(entry);
		} else {
			assert !entry.isParent();
			existing.addToTail(entry);
			if (entry.getFile() != null)
				getFileSet(entry.getFile()).add(existing);
		}
	}
	
	public void remove(SemanticIndexEntry entry) {
		// Remove from table
		SemanticIndexEntry head;
		List<SemanticIndexEntry> tail = entry.getTail();
		if (!tail.isEmpty()) {
			head = tail.remove(tail.size() - 1);
			head.setTail(tail);
			table.put(head, head);
		} else if ((head = table.get(entry)) != entry) {
			tail = head.getTail();
			for (int i = 0, max = tail.size(); i < max; i++) {
				if (tail.get(i) == entry) {
					tail.remove(i);
					break;
				}
			}
		} else {
			// Common case: only one entry with this id exists
			table.remove(entry);
		}
		
		boolean otherEntriesExist = head != entry;
		
		// Remove from parent
		SemanticIndexEntryParent parent = getEntryParentAbove(entry.getNamespace(), entry.getId(), false);
		if (parent != null) {
			if (!otherEntriesExist) {
				parent.remove(entry);
				if (parent.isEmpty()) remove(parent);
			} else {
				parent.add(head); // overwrite with head
			}
		}
			
		// Remove from fileTable
		URI file = entry.getFile();
		if (file != null) {
			Set<SemanticIndexEntry> fileSet = getFileSet(file);
			if (fileSet == null) {
				// Already removed by clear(URI)
			} if (otherEntriesExist && isFileReferenced(head, tail, file)) {
				fileSet.add(head); // overwrite with head
			} else {
				fileSet.remove(entry);
				if (fileSet.isEmpty())
					fileTable.remove(file);
			}
		}
	}

	private boolean isFileReferenced(SemanticIndexEntry head, List<SemanticIndexEntry> tail, URI file) {
		if (file.equals(head.getFile()))
			return true;
		for (int i = 0, max = tail.size(); i < max; i++) {
			if (file.equals(tail.get(i).getFile()))
				return true;
		}
		return false;
	}
	
	private Set<SemanticIndexEntry> getFileSet(URI file) {
		Set<SemanticIndexEntry> result = fileTable.get(file);
		if (result == null) {
			result = new HashSet<SemanticIndexEntry>();
			fileTable.put(file, result);
		}
		return result;
	}
	
	public IStrategoList getTerms(IStrategoAppl template) {
		IStrategoList results = termFactory.makeList();
		SemanticIndexEntry entry = getEntry(template);
		if (entry == null) return results;
		
		IStrategoAppl result = entry.toTerm(factory);
		results = termFactory.makeListCons(result, results);
		List<SemanticIndexEntry> tail = entry.getTail();
		
		for (int i = 0, max = tail.size(); i < max; i++) {
			result = tail.get(i).toTerm(factory);
			results = termFactory.makeListCons(result, results);
		}
		
		return results;
	}
	
	/**
	 * Returns an entry in the index that matches the given template.
	 * Note that the result can have a 'tail' with other matching entries.
	 */
	public SemanticIndexEntry getEntry(IStrategoAppl template) {
		ensureInitialized();
		return getEntry(factory.getEntryType(template),
				factory.getEntryNamespace(template),
				factory.getEntryId(template),
				factory.getEntryData(template) != null
				);
	}
	
	/**
	 * Returns an entry in the index that matches the given type and id.
	 * Note that the result can have a 'tail' with other matching entries.
	 */
	private SemanticIndexEntry getEntry(IStrategoTerm type, IStrategoTerm namespace, IStrategoList id, boolean isDataEntry) {
		entryTemplate.internalReinit(type, namespace, id, isDataEntry ? factory.getDefDataCon() : null);
		return table.get(entryTemplate);
	}
	
	public IStrategoList getEntryChildTerms(IStrategoAppl template) {
		ensureInitialized();
		IStrategoTerm type = factory.getEntryType(template);
		IStrategoTerm namespace = factory.getEntryNamespace(template);
		SemanticIndexEntryParent parent = getEntryParentAt(namespace, factory.getEntryId(template));
		if (parent == null)
			return termFactory.makeList();
		if (type == factory.getDefCon() && parent.getAllDefsCached() != null)
			return parent.getAllDefsCached();
		IStrategoList results = termFactory.makeList();
		for (SemanticIndexEntry entry : parent.getChildren()) {
			if (entry.getType() == type) {
				assert !entry.isParent();
				assert entry.getNamespace().match(namespace);
				results = termFactory.makeListCons(entry.toTerm(factory), results);
			}
		}
		if (type == factory.getDefCon())
			parent.setAllDefsCached(results);
		return results;
	}
	
	public IStrategoList getEntryDescendantTerms(IStrategoAppl template) {
		ensureInitialized();
		IStrategoTerm type = factory.getEntryType(template);
		IStrategoTerm namespace = factory.getEntryNamespace(template);
		SemanticIndexEntryParent parent = getEntryParentAt(namespace, factory.getEntryId(template));
		return collectEntryDescendentTerms(parent, type, namespace, termFactory.makeList());
	}
	
	private IStrategoList collectEntryDescendentTerms(SemanticIndexEntryParent parent, IStrategoTerm type, IStrategoTerm namespace, IStrategoList results) {
		for (SemanticIndexEntry entry : parent.getChildren()) {
			if (entry.getType() == type) {
				assert !entry.isParent();
				assert entry.getNamespace().match(namespace);
				results = termFactory.makeListCons(entry.toTerm(factory), results);
			} else if (entry.isParent()) {
				results = collectEntryDescendentTerms((SemanticIndexEntryParent) entry, type, namespace, results);
			}
		}
		return results;
	}
	
	private SemanticIndexEntryParent getEntryParentAbove(IStrategoTerm namespace, IStrategoList id, boolean createNonExistant) {
		if (id.isEmpty()) {
			return null;
		} else {
			id = id.tail();
		}
		SemanticIndexEntryParent result = getEntryParentAt(namespace, id);
		if (result == null && createNonExistant) {
			result = factory.createEntryParent(namespace, id, getEntryParentAbove(namespace, id, true));
			add(result); // add and recurse for parents
		}
		return result;
	}
	
	/**
	 * Gets the {@link SemanticIndexEntryParent} with the given identifier.
	 */
	private SemanticIndexEntryParent getEntryParentAt(IStrategoTerm namespace, IStrategoList id) {
		return (SemanticIndexEntryParent) getEntry(SemanticIndexEntryParent.TYPE, namespace, id, false);
	}
	
	public void clear() {
		table.clear();
		fileTable.clear();
	}
	
	public void clear(URI file) {
		Set<SemanticIndexEntry> fileSet = fileTable.remove(file);
		if (fileSet == null) return;
		
		SemanticIndexEntry[] copy = new SemanticIndexEntry[fileSet.size()];
		copy = fileSet.toArray(copy);
		for (SemanticIndexEntry entry : copy) {
			remove(entry);
		}
	}
	
	public boolean isIndexed(URI file) {
		return fileTable.get(file) != null;
	}
	
	public void setIndexed(URI file) {
		getFileSet(file); // creates new file set
	}
	
	public Set<URI> getAllFiles() {
		return Collections.unmodifiableSet(fileTable.keySet());
	}
	
	public URI toFileURI(String path) {
		return toFileURI(path, agent);
	}

	public static URI toFileURI(String path, IOAgent agent) {
		File file = new File(path);
		return file.isAbsolute()
			? file.toURI()
			: new File(agent.getWorkingDir(), path).toURI();
	}
	
	public String fromFileURI(URI uri) {
		File file = new File(uri);
		return file.toString();
	}
	
	@Override
	public String toString() {
		return table.keySet().toString();
	}
	

}
