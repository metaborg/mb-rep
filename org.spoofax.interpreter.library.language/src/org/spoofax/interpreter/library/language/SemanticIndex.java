package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermList;
import static org.spoofax.terms.Term.termAt;
import static org.spoofax.terms.Term.tryGetConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

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
 * @author Gabri�l Konat
 */
public class SemanticIndex implements ISemanticIndex {
	private final Multimap<SemanticIndexURI, SemanticIndexEntry> entries = 
			ArrayListMultimap.create();
	private final Multimap<SemanticIndexURI, SemanticIndexEntry> childs = 
			ArrayListMultimap.create();
	private final Map<SemanticIndexFile, SemanticIndexFile> files =
			new HashMap<SemanticIndexFile, SemanticIndexFile>();
	
	private static final IStrategoConstructor FILE_ENTRIES_CON =
			new TermFactory().makeConstructor("FileEntries", 2);
	
	private IOAgent agent;
	private AtomicLong revisionProvider;
	private ITermFactory termFactory;
	private SemanticIndexEntryFactory factory;
	
	public void initialize(ITermFactory factory, IOAgent agent, AtomicLong revisionProvider) {
		this.agent = agent;
		this.factory = new SemanticIndexEntryFactory(factory);
		this.termFactory = factory;
		this.revisionProvider = revisionProvider;
	}

	public void ensureInitialized() {
		if (factory == null)
			throw new IllegalStateException("Semantic index not initialized");
	}

	public SemanticIndexEntryFactory getFactory() {
		return factory;
	}
	
	public void add(IStrategoAppl entry, SemanticIndexFile file) {
		assert getFile(file) == file : "Files must be maximally shared: use getFile() to get a shared File reference";
		ensureInitialized();
		
		IStrategoConstructor constructor = entry.getConstructor();
		IStrategoTerm contentsType = factory.getEntryContentsType(entry);
		IStrategoList id = factory.getEntryId(entry);
		IStrategoTerm namespace = factory.getEntryNamespace(entry);
		IStrategoTerm contents = factory.getEntryContents(entry);
		
		SemanticIndexEntry newEntry = 
				factory.createEntry(constructor, namespace, id, contentsType, contents, file);

		add(newEntry);
	}

	public void addAll(IStrategoList entries, SemanticIndexFile file) {
		while(!entries.isEmpty()) {
			add((IStrategoAppl) entries.head(), file);
			entries = entries.tail();
		}
	}
	
	private void add(SemanticIndexEntry entry) {
		entries.put(entry.getURI(), entry);
		
		// Add entry to childs.
		SemanticIndexURI parent = entry.getURI().getParent();
		if(parent != null)
			childs.put(entry.getURI().getParent(), entry);

		// Add entry to files.
		entry.getFile().addEntry(entry);
		entry.getFile().setTimeRevision(new Date(), revisionProvider.incrementAndGet());
	}

	public void remove(IStrategoAppl template) {
		remove(factory.createURIFromTemplate(template));
	}
	
	public void remove(IStrategoAppl template, SemanticIndexFile file) {
		SemanticIndexURI uri = factory.createURIFromTemplate(template);
		Collection<SemanticIndexEntry> candidateEntries = entries.get(uri);
		SemanticIndexEntry[] copy = candidateEntries.toArray(new SemanticIndexEntry[candidateEntries.size()]);
		List<SemanticIndexEntry> entriesToRemove = new ArrayList<SemanticIndexEntry>();
		for(SemanticIndexEntry entry : copy) {
			if(entry.getFile() == file) {
				entriesToRemove.add(entry);
				entries.remove(uri, entry);
			}
		}
		
		remove(uri, entriesToRemove);
	}
	
	/**
	 * Removes all entries with given URI.
	 * 
	 * @param uri	The URI to remove all entries for.
	 */
	private void remove(SemanticIndexURI uri) {
		Collection<SemanticIndexEntry> removedEntries = entries.removeAll(uri);
		
		remove(uri, removedEntries);
	}
	
	/**
	 * Removes given entries from the childs multimap and file lists. The given entries
	 * should already be removed from the entries multimap.
	 * 
	 * @param uri				The common URI of the removed elements.
	 * @param removedEntries	The removed entries.
	 */
	private void remove(SemanticIndexURI uri, Collection<SemanticIndexEntry> removedEntries) {
		// Remove entry from childs.
		// TODO: Linear run-time, can be improved with an inverse childs map?
		SemanticIndexURI parentURI = uri.getParent();
		for(SemanticIndexEntry entry : removedEntries) {
			childs.remove(parentURI, entry);
		}
		
		// Remove entry from files.
		for(SemanticIndexEntry entry : removedEntries) {
			SemanticIndexFile file = entry.getFile();
			if (file != null) {
				// TODO: Linear run-time (in SemanticIndexfile), can be improved with a HashMultimap?
				file.removeEntry(entry); 
				file.setTimeRevision(new Date(), revisionProvider.incrementAndGet());
				
				// Remove file if there are no entries left in it.
				if(file.getEntries().isEmpty())
					removeSharedFile(getFile(file));
			}
		}
	}
	
	/**
	 * Removes a single entry.
	 * 
	 * @param entry	The entry to remove.
	 */
	private void remove(SemanticIndexEntry entry) {
		SemanticIndexURI uri = entry.getURI();
		boolean removed = entries.remove(uri, entry);
		
		if(removed)
			remove(uri, Arrays.asList(new SemanticIndexEntry[]{entry}));
	}
	
	public Collection<SemanticIndexEntry> getEntries(IStrategoAppl template) {
		return entries.get(factory.createURIFromTemplate(template));
	}

	public Collection<SemanticIndexEntry> getEntryChildTerms(IStrategoAppl template) {
		return childs.get(factory.createURIFromTemplate(template));
	}
	
	public SemanticIndexFile getFile(IStrategoTerm fileTerm) {
		return getFile(SemanticIndexFile.fromTerm(agent, fileTerm));
	}
	
	/**
	 * Gets a maximally shared file reference equal to the given file.
	 */
	private SemanticIndexFile getFile(SemanticIndexFile file) {
		SemanticIndexFile sharedFile = files.get(file);
		if(sharedFile == null) {
			files.put(file, file);
			return file;
		}
		return sharedFile;
	}
	
	public void removeFile(IStrategoTerm fileTerm) {
		SemanticIndexFile file = getFile(fileTerm);
		
		clearFile(file);
		removeSharedFile(file);
	}
	
	private void clearFile(SemanticIndexFile file) {
		assert files.get(file) == null || files.get(file) == file;	// Require maximally shared file or null file.
		
		List<SemanticIndexEntry> fileEntries = file.getEntries();
		if (fileEntries.isEmpty()) return;
		
		// Remove every entry in this file from the index.
		// TODO: Add more efficient remove operation for removing multiple entries.
		SemanticIndexEntry[] copy = fileEntries.toArray(new SemanticIndexEntry[fileEntries.size()]);
		for(SemanticIndexEntry entry : copy) {
			// TODO: Assert doesn't work, should it be here?
		    //assert entries.get(entry.getURI()).size() != 0;
			remove(entry);
		}
		
		assert file.getEntries().isEmpty();
	}
	
	private void removeSharedFile(SemanticIndexFile file) {
		assert file.getEntries().isEmpty(); // Only allow removing a shared file if it has no entries.
		
		files.remove(file);
	}
	
	public Collection<SemanticIndexFile> getAllFiles() {
		return files.values();
	}
	
	public void clear() {
		entries.clear();
		childs.clear();
		files.clear();
	}
	
	public IStrategoTerm toTerm(boolean includePositions) {
		IStrategoList results = termFactory.makeList();
		for (SemanticIndexFile file : files.keySet()) {
			IStrategoList fileResults = SemanticIndexEntry.toTerms(termFactory, file.getEntries());
			// TODO: include time stamp for file
			IStrategoTerm result = termFactory.makeAppl(FILE_ENTRIES_CON, file.toTerm(termFactory), fileResults);
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
			result.initialize(factory, agent, new AtomicLong());
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
				SemanticIndexFile file = getFile(termAt(fileEntries, 0));
				addAll((IStrategoList) termAt(fileEntries, 1), file);
			} catch (IllegalStateException e) {
				throw new IllegalStateException(e);
			} catch (RuntimeException e) { // HACK: catch all runtime exceptions
				throw new IOException("Unexpected exception reading index: " + e);
			}
		} else {
			throw new IOException("Illegal index entry: " + fileEntries);
		}
	}

}
