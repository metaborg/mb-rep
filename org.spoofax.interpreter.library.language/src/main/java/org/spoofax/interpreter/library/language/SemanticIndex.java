package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermList;
import static org.spoofax.terms.Term.termAt;
import static org.spoofax.terms.Term.tryGetConstructor;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;
import org.spoofax.terms.TermFactory;
import org.spoofax.terms.attachments.TermAttachmentSerializer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Gabriel Konat
 */
public class SemanticIndex implements ISemanticIndex {
	public static final boolean DEBUG_ENABLED = SemanticIndex.class.desiredAssertionStatus();
	
	private final Multimap<SemanticIndexURI, SemanticIndexEntry> entries = 
			ArrayListMultimap.create();
	private final Multimap<SemanticIndexURI, SemanticIndexEntry> childs = 
			ArrayListMultimap.create();
	private final Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> entriesPerFileDescriptor = 
			ArrayListMultimap.create();
	private final Multimap<URI, SemanticIndexEntry> entriesPerURI = 
			ArrayListMultimap.create();
	private final Multimap<IStrategoList, SemanticIndexEntry> entriesPerSubfile = 
			ArrayListMultimap.create();
	private final Map<SemanticIndexFileDescriptor, SemanticIndexFile> files =
			new HashMap<SemanticIndexFileDescriptor, SemanticIndexFile>();
	
	private static final IStrategoConstructor FILE_ENTRIES_CON =
			new TermFactory().makeConstructor("FileEntries", 2);
	
	private IOAgent agent;
	private ITermFactory termFactory;
	private SemanticIndexEntryFactory factory;
	
	public void initialize(ITermFactory factory, IOAgent agent) {
		this.agent = agent;
		this.factory = new SemanticIndexEntryFactory(factory);
		this.termFactory = factory;
	}

	public void ensureInitialized() {
		if (factory == null)
			throw new IllegalStateException("Semantic index not initialized");
	}

	public SemanticIndexEntryFactory getFactory() {
		return factory;
	}
	
	public void add(IStrategoAppl entry, SemanticIndexFileDescriptor fileDescriptor) {
		ensureInitialized();
		
		IStrategoConstructor constructor = entry.getConstructor();
		IStrategoTerm contentsType = factory.getEntryContentsType(entry);
		IStrategoList id = factory.getEntryId(entry);
		IStrategoTerm namespace = factory.getEntryNamespace(entry);
		IStrategoTerm contents = factory.getEntryContents(entry);
		
		SemanticIndexEntry newEntry = 
				factory.createEntry(constructor, namespace, id, contentsType, contents, fileDescriptor);

		add(newEntry);
	}

	public void add(SemanticIndexEntry entry) {
		addOrGetFile(entry.getFileDescriptor());
		
		entries.put(entry.getURI(), entry);
		
		// Add entry to childs.
		SemanticIndexURI parent = entry.getURI().getParent();
		if(parent != null)
			childs.put(entry.getURI().getParent(), entry);

		// Add entry to files.
		entriesPerFileDescriptor.put(entry.getFileDescriptor(), entry);
		entriesPerURI.put(entry.getFileDescriptor().getURI(), entry);
		entriesPerSubfile.put(entry.getFileDescriptor().getSubfile(), entry);
	}
	
	public void addAll(IStrategoList entries, SemanticIndexFileDescriptor fileDescriptor) {
		while(!entries.isEmpty()) {
			add((IStrategoAppl) entries.head(), fileDescriptor);
			entries = entries.tail();
		}
	}
	
	public void remove(IStrategoAppl template, SemanticIndexFileDescriptor fileDescriptor) {
		SemanticIndexURI uri = factory.createURIFromTemplate(template);
		Collection<SemanticIndexEntry> candidateEntries = entries.get(uri);
		SemanticIndexEntry[] copy = candidateEntries.toArray(new SemanticIndexEntry[candidateEntries.size()]);
		List<SemanticIndexEntry> entriesToRemove = new ArrayList<SemanticIndexEntry>();
		for(SemanticIndexEntry entry : copy) {
			if(entry.getFileDescriptor().equals(fileDescriptor)) {
				entriesToRemove.add(entry);
				entries.remove(uri, entry);
			}
		}
		
		removeFinal(entriesToRemove);
	}
	
	/**
	 * Removes a single entry.
	 * 
	 * @param entry	The entry to remove.
	 */
	@SuppressWarnings("unused")
	private void remove(SemanticIndexEntry entry) {
		boolean removed = entries.remove(entry.getURI(), entry);
		
		if(removed)
			removeFinal(Arrays.asList(new SemanticIndexEntry[]{entry}));
	}
	
	/**
	 * Removes entries.
	 * 
	 * @param entriesToRemove	The entries to remove.
	 */
	private void remove(Collection<SemanticIndexEntry> entriesToRemove) {
		List<SemanticIndexEntry> removedEntries = new ArrayList<SemanticIndexEntry>(entriesToRemove.size());
		for(SemanticIndexEntry entry : entriesToRemove) {
			if(entries.remove(entry.getURI(), entry))
				removedEntries.add(entry);
		}

		removeFinal(removedEntries);
	}
	
	/**
	 * Removes given entries from the childs multimap and file lists. The given entries
	 * should already be removed from the entries multimap.
	 * 
	 * @param removedEntries	The removed entries.
	 */
	private void removeFinal(Collection<SemanticIndexEntry> removedEntries) {
		for(SemanticIndexEntry entry : removedEntries) {
			// Remove entry from childs.
			// TODO: Linear run-time (because of for loop), can be improved with an inverse childs map?
			childs.remove(entry.getURI().getParent(), entry);
			
			// Remove entry from files.
			SemanticIndexFileDescriptor fileDescriptor = entry.getFileDescriptor();
			if (fileDescriptor != null)
			{
				entriesPerFileDescriptor.remove(fileDescriptor, entry);
				entriesPerURI.remove(fileDescriptor.getURI(), entry);
				entriesPerSubfile.remove(fileDescriptor.getSubfile(), entry);
			}
		}
	}
	
	public Collection<SemanticIndexEntry> getEntries(IStrategoAppl template) {
	  SemanticIndexURI uri = factory.createURIFromTemplate(template);
		return getCollection(entries.get(uri));
	}

	public Collection<SemanticIndexEntry> getEntryChildTerms(IStrategoAppl template) {
	  SemanticIndexURI uri = factory.createURIFromTemplate(template);
		return getCollection(childs.get(uri));
	}
	
	public Collection<SemanticIndexEntry> getEntriesInFile(SemanticIndexFileDescriptor fileDescriptor) {
		if(fileDescriptor.getSubfile() == null)
			return getCollection(entriesPerURI.get(fileDescriptor.getURI()));
		else if(fileDescriptor.getURI() == null)
			return getCollection(entriesPerSubfile.get(fileDescriptor.getSubfile()));
		else
			return getCollection(entriesPerFileDescriptor.get(fileDescriptor));
	}
	
	public Collection<SemanticIndexEntry> getAllEntries() {
		return getCollection(entries.values());
	}
	
	public SemanticIndexFile getFile(SemanticIndexFileDescriptor fileDescriptor) {
		return addOrGetFile(fileDescriptor);
	}
	
	private SemanticIndexFile addOrGetFile(SemanticIndexFileDescriptor fileDescriptor) {
		SemanticIndexFile file = files.get(fileDescriptor);
		if(file == null) {
			file = new SemanticIndexFile(fileDescriptor, null);
			files.put(fileDescriptor, file);
		}
		return file;
	}
	
	public SemanticIndexFileDescriptor getFileDescriptor(IStrategoTerm fileTerm) {
		return SemanticIndexFileDescriptor.fromTerm(agent, fileTerm);
	}
	
	public void removeFile(IStrategoTerm fileTerm) {
		removeFile(getFileDescriptor(fileTerm));
	}
	
	public void removeFile(SemanticIndexFileDescriptor fileDescriptor) {
		clearFile(fileDescriptor);
	}
	
	private void clearFile(SemanticIndexFileDescriptor fileDescriptor) {
		Collection<SemanticIndexEntry> fileEntries = getEntriesInFile(fileDescriptor);
		if (fileEntries.isEmpty()) 
			return;
		
		// Remove every entry in this file from the index.
		SemanticIndexEntry[] copy = fileEntries.toArray(new SemanticIndexEntry[0]);
		remove(Arrays.asList(copy));

		assert getEntriesInFile(fileDescriptor).isEmpty();
	}
	
	public Collection<SemanticIndexFile> getAllFiles() {
		return getCollection(files.values());
	}
	
	public Collection<SemanticIndexFileDescriptor> getAllFileDescriptors() {
		return getCollection(files.keySet());
	}
	
	public void clear() {
		entries.clear();
		childs.clear();
		entriesPerFileDescriptor.clear();
		entriesPerURI.clear();
		entriesPerSubfile.clear();
		files.clear();
	}
	
	public IStrategoTerm toTerm(boolean includePositions) {
		IStrategoList results = termFactory.makeList();
		for(SemanticIndexFileDescriptor fileDescriptor : files.keySet()) {
			IStrategoList fileResults = SemanticIndexEntry.toTerms(termFactory, entriesPerFileDescriptor.get(fileDescriptor));
			// TODO: include time stamp for file
			IStrategoTerm result = termFactory.makeAppl(FILE_ENTRIES_CON, fileDescriptor.toTerm(termFactory), fileResults);
			results = termFactory.makeListCons(result, results);
		}
		
		if (includePositions) {
			// TODO: optimize -- store more compact attachments for positions
			TermFactory simpleFactory = new TermFactory();
			TermAttachmentSerializer serializer = new TermAttachmentSerializer(simpleFactory);
			results = (IStrategoList) serializer.toAnnotations(results);
		}
		
		return results;
	}
	
	public static SemanticIndex fromTerm(IStrategoTerm term, ITermFactory factory, IOAgent agent,
			boolean extractPositions) throws IOException {
		
		if (extractPositions) {
			TermAttachmentSerializer serializer = new TermAttachmentSerializer(factory);
			term = (IStrategoList) serializer.fromAnnotations(term, false);
		}
		
		if (isTermList(term)) {
			SemanticIndex result = new SemanticIndex();
			result.initialize(factory, agent);
			for (IStrategoList list = (IStrategoList) term; !list.isEmpty(); list = list.tail()) {
				result.loadFileEntriesTerm(list.head());
			}
			return result;
		} else {
			throw new IOException("Expected list of " + FILE_ENTRIES_CON.getName());
		}
	}
	
	private void loadFileEntriesTerm(IStrategoTerm fileEntries) throws IOException {
		if (tryGetConstructor(fileEntries) == FILE_ENTRIES_CON) {
			try {
				addAll((IStrategoList) termAt(fileEntries, 1), getFileDescriptor(termAt(fileEntries, 0)));
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			} catch (RuntimeException e) { // HACK: catch all runtime exceptions
				throw new IOException("Unexpected exception reading index: " + e);
			}
		} else {
			throw new IOException("Illegal index entry: " + fileEntries);
		}
	}
	
	/**
	 * Returns an unmodifiable collection if in debug mode, or the collection if not.
	 */
	private static final <T> Collection<T> getCollection(Collection<T> collection) {
		if(DEBUG_ENABLED) {
			return Collections.unmodifiableCollection(collection);
		} else {
			return collection;
		}
	}
}
