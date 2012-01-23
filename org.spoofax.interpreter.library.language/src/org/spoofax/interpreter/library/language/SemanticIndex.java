package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.library.language.SemanticIndexFile.DEFAULT_DESCRIPTOR;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndex {
	
	private final Map<SemanticIndexEntry, SemanticIndexEntry> table =
		new HashMap<SemanticIndexEntry, SemanticIndexEntry>();
	
	private final Map<URI, SemanticIndexFile> files =
		new HashMap<URI, SemanticIndexFile>();

	private IOAgent agent;

	private ITermFactory termFactory;
	
	private SemanticIndexEntryFactory factory;
	
	private SemanticIndexEntry entryTemplate;
	
	private Date updatesTime; // TODO: use updatesTime for time stamping updated files
	
	public void initialize(ITermFactory factory, IOAgent agent) {
		this.agent = agent;
		this.factory = new SemanticIndexEntryFactory(factory);
		this.termFactory = factory;
		entryTemplate = new SemanticIndexEntry(
			factory.makeConstructor("template", 0), factory.makeList(), factory.makeList(),
			null, null, null);
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
		IStrategoTerm contentsType = factory.getEntryContentsType(entry);
		IStrategoList id = factory.getEntryId(entry);
		IStrategoTerm namespace = factory.getEntryNamespace(entry);
		IStrategoTerm contents = factory.getEntryContents(entry);
		SemanticIndexEntryParent parent = getEntryParentAbove(namespace, id, true);
		SemanticIndexFile semFile = getFile(file);
		add(factory.createEntry(entry.getConstructor(), namespace, id, contentsType, contents, parent, semFile), parent);
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
				entry.getFile().getEntries().add(entry);
		} else {
			assert !entry.isParent() && existing != entry;
			existing.getLast().setNext(entry);
			if (entry.getFile() != null)
				entry.getFile().getEntries().add(entry);
		}
		if (entry.getFile() != null)
			entry.getFile().setTime(updatesTime);
	}
	
	/**
	 * Removes a single entry (not a list of entries).
	 */
	public void remove(SemanticIndexEntry entry) {
		// Remove from table
		SemanticIndexEntry head = table.get(entry);
		if (head == null) {
		    return;
		} else if (head == entry) {
			table.remove(entry);
			if (entry.getNext() != null)
				table.put(entry.getNext(), entry.getNext());
		} else {
			head.setNext(entry.getNext());
		}
		
		// Remove from parent
		SemanticIndexEntryParent parent = getEntryParentAbove(entry.getNamespace(), entry.getId(), false);
		if (parent != null)
			parent.remove(entry);
			
		// Remove from fileTable
		SemanticIndexFile file = entry.getFile();
		if (file != null) {
			file.getEntries().remove(entry);
			entry.getFile().setTime(updatesTime);
		}
	}
	
	/**
	 * Gets a {@link SemanticIndexFile}.
	 * Creates it if it didn't exist yet.
	 */
	public SemanticIndexFile getFile(URI file) {
		SemanticIndexFile result = files.get(file);
		if (result == null) {
			result = new SemanticIndexFile(file, DEFAULT_DESCRIPTOR, updatesTime);
			files.put(file, result);
		}
		return result;
	}
	
	/**
	 * Returns an entry in the index that matches the given template.
	 * Note that the result can have a 'tail' with other matching entries.
	 */
	public SemanticIndexEntry getEntries(IStrategoAppl template) {
		ensureInitialized();
		return getEntries(template.getConstructor(),
				factory.getEntryNamespace(template),
				factory.getEntryId(template),
				factory.getEntryContentsType(template)
				);
	}
	
	/**
	 * Returns an entry in the index that matches the given type and id.
	 * Note that the result can have a 'tail' with other matching entries.
	 */
	private SemanticIndexEntry getEntries(IStrategoConstructor constructor, IStrategoTerm namespace, IStrategoList id, IStrategoTerm contentsType) {
		entryTemplate.internalReinit(constructor, namespace, id, contentsType);
		return table.get(entryTemplate);
	}
	
	public IStrategoList getEntryChildTerms(IStrategoAppl template) {
		ensureInitialized();
		IStrategoConstructor constructor = template.getConstructor();
		IStrategoTerm namespace = factory.getEntryNamespace(template);
		SemanticIndexEntryParent parent = getEntryParentAt(namespace, factory.getEntryId(template));
		if (parent == null)
			return termFactory.makeList();
		if (constructor == factory.getDefCon() && parent.getAllDefsCached() != null)
			return parent.getAllDefsCached();
		IStrategoList results = termFactory.makeList();
		for (SemanticIndexEntry entry : parent.getChildren()) {
			if (entry.getConstructor() == constructor) {
				assert !entry.isParent();
				assert entry.getNamespace().match(namespace);
				results = termFactory.makeListCons(entry.toTerm(factory.getTermFactory()), results);
			}
		}
		if (constructor == factory.getDefCon())
			parent.setAllDefsCached(results);
		return results;
	}
	
	public IStrategoList getEntryDescendantTerms(IStrategoAppl template) {
		ensureInitialized();
		IStrategoConstructor constructor = template.getConstructor();
		IStrategoTerm namespace = factory.getEntryNamespace(template);
		SemanticIndexEntryParent parent = getEntryParentAt(namespace, factory.getEntryId(template));
		return collectEntryDescendentTerms(parent, constructor, namespace, termFactory.makeList());
	}
	
	private IStrategoList collectEntryDescendentTerms(SemanticIndexEntryParent parent, IStrategoConstructor constructor,
			IStrategoTerm namespace, IStrategoList results) {
		for (SemanticIndexEntry entry : parent.getChildren()) {
			if (entry.getConstructor() == constructor) {
				assert !entry.isParent();
				assert entry.getNamespace().match(namespace);
				results = termFactory.makeListCons(entry.toTerm(factory.getTermFactory()), results);
			} else if (entry.isParent()) {
				results = collectEntryDescendentTerms((SemanticIndexEntryParent) entry, constructor, namespace, results);
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
			// add initial entry (that stores our time stamp)
			result = factory.createEntryParent(namespace, id, getEntryParentAbove(namespace, id, true));
			add(result); // add and recurse for parents
		}
		return result;
	}
	
	/**
	 * Gets the {@link SemanticIndexEntryParent} with the given identifier.
	 */
	private SemanticIndexEntryParent getEntryParentAt(IStrategoTerm namespace, IStrategoList id) {
		return (SemanticIndexEntryParent) getEntries(SemanticIndexEntryParent.CONSTRUCTOR, namespace, id, null);
	}
	
	public void clear() {
		table.clear();
		files.clear();
	}
	
	public IStrategoTerm toTerm() {
		ITermFactory terms = factory.getTermFactory();
		IStrategoList entries = SemanticIndexEntry.toTerms(terms, table.values());
		return entries;
	}
	
	public void clear(SemanticIndexFile file) {
		Set<SemanticIndexEntry> fileSet = file.getEntries();
		if (fileSet.isEmpty()) return;
		
		SemanticIndexEntry[] copy = new SemanticIndexEntry[fileSet.size()];
		copy = fileSet.toArray(copy);
		for (SemanticIndexEntry entry : copy) {
		    assert table.get(entry) != null;
			remove(entry);
		}
	}
	
	public Collection<SemanticIndexFile> getAllFiles() {
		return files.values();
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
