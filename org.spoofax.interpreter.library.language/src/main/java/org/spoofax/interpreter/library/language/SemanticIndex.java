package org.spoofax.interpreter.library.language;

import static org.spoofax.interpreter.core.Tools.isTermList;
import static org.spoofax.terms.Term.termAt;
import static org.spoofax.terms.Term.tryGetConstructor;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * @author Gabriel Konat
 */
public class SemanticIndex implements ISemanticIndex {
	public static final boolean DEBUG_ENABLED = SemanticIndex.class.desiredAssertionStatus();
	
	//private static final int expectedDistinctKeys = 50000;
	private static final int expectedDistinctPartitions = 100;
	private static final int expectedValuesPerPartition = 1000;
	
	private final Map<SemanticIndexURI, Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry>> entries = 
	    new HashMap<SemanticIndexURI, Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry>>();
	private final Map<SemanticIndexURI, Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry>> childs = 
	    new HashMap<SemanticIndexURI, Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry>>();
	private final Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> entriesPerFileDescriptor = 
	    LinkedHashMultimap.create();
	private final Multimap<URI, SemanticIndexEntry> entriesPerURI = 
	    LinkedHashMultimap.create();
	private final Multimap<IStrategoList, SemanticIndexEntry> entriesPerSubfile = 
	    LinkedHashMultimap.create();
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
	
  private Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> innerEntries(
      SemanticIndexURI uri)
  {
    Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> innerMap = 
        entries.get(uri);
    
    if(innerMap != null)
      return innerMap;
    
    innerMap = ArrayListMultimap.create(expectedDistinctPartitions, 
        expectedValuesPerPartition);
    entries.put(uri, innerMap);
    return innerMap;
  }
  
  private Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> innerChildEntries(
      SemanticIndexURI uri)
  {
    Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> innerMap = 
        childs.get(uri);
    
    if(innerMap != null)
      return innerMap;
    
    innerMap = ArrayListMultimap.create(expectedDistinctPartitions, 
        expectedValuesPerPartition);
    childs.put(uri, innerMap);
    return innerMap;
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
	  final SemanticIndexFileDescriptor file = entry.getFileDescriptor();
	  final SemanticIndexURI uri = entry.getURI();
	  
		addOrGetFile(file);
		
		innerEntries(uri).put(file, entry);
		
		// Add entry to childs.
		SemanticIndexURI parent = uri.getParent();
		if(parent != null)
		  innerChildEntries(parent).put(file, entry);

		// Add entry to files.
		entriesPerFileDescriptor.put(file, entry);
		entriesPerURI.put(file.getURI(), entry);
		entriesPerSubfile.put(file.getSubfile(), entry);
	}
	
	public void addAll(IStrategoList entries, SemanticIndexFileDescriptor fileDescriptor) {
		while(!entries.isEmpty()) {
			add((IStrategoAppl) entries.head(), fileDescriptor);
			entries = entries.tail();
		}
	}
	
	public Collection<SemanticIndexEntry> getEntries(IStrategoAppl template) {
	  SemanticIndexURI uri = factory.createURIFromTemplate(template);
		return getCollection(innerEntries(uri).values());
	}

	public Collection<SemanticIndexEntry> getEntryChildTerms(IStrategoAppl template) {
	  SemanticIndexURI uri = factory.createURIFromTemplate(template);
		return getCollection(innerChildEntries(uri).values());
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
	  List<SemanticIndexEntry> allEntries = new LinkedList<SemanticIndexEntry>();
		Collection<Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry>> values = entries.values();
		for(Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> map : values)
		  allEntries.addAll(map.values());
		
		return allEntries;
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
	  assert fileDescriptor.getSubfile() != null || fileDescriptor.getURI() != null;
	  
	  Collection<Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry>> values = entries.values();
    for(Multimap<SemanticIndexFileDescriptor, SemanticIndexEntry> map : values)
      map.removeAll(fileDescriptor);
    
    if(fileDescriptor.getSubfile() == null)
      entriesPerURI.removeAll(fileDescriptor.getURI());
    else if(fileDescriptor.getURI() == null)
      entriesPerSubfile.removeAll(fileDescriptor.getSubfile());
    else
    {
      entriesPerFileDescriptor.removeAll(fileDescriptor);
      clearFile(new SemanticIndexFileDescriptor(fileDescriptor.getURI(), null));
      clearFile(new SemanticIndexFileDescriptor(null, fileDescriptor.getSubfile()));
    }
    
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
