package org.spoofax.interpreter.library.language;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.spoofax.interpreter.library.IOAgent;
import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.interpreter.terms.ITermFactory;

/**
 * @author Gabriël Konat
 */
public interface ISemanticIndex {

	/**
	 * Initializes this semantic index.
	 * 
	 * @param revisionProvider  An atomic revision number provider, should be shared between copies
	 *                          of the same index.
	 */
	public abstract void initialize(ITermFactory factory, IOAgent agent,
			AtomicLong revisionProvider);

	/**
	 * Gets the entry factory used by this semantic index.
	 */
	public abstract SemanticIndexEntryFactory getFactory();

	/**
	 * Adds a new entry to the index.
	 * 
	 * @param entry	The entry to add.
	 * @param file  The file to associate the entry with.
	 */
	public abstract void add(IStrategoAppl entry, SemanticIndexFile file);

	/**
	 * Adds a list of entries to the index.
	 * 
	 * @param entries	The entries to add.
	 * @param file		The file to associate the entries with.
	 */
	public abstract void addAll(IStrategoList entries, SemanticIndexFile file);

	/**
	 * Removes all entries that match given template.
	 * 
	 * @param template	The template to match entries against.
	 */
	public abstract void remove(IStrategoAppl template);
	
	/**
	 * Removes all entries that match given template and are from given file.
	 * 
	 * @param template	The template to match entries against.
	 * @param file		The file entries must be from.
	 */
	public abstract void remove(IStrategoAppl template, SemanticIndexFile file);
	
	/**
	 * Gets all entries that match given template.
	 * 
	 * @param template	The template to match entries against.
	 */
	public abstract Collection<SemanticIndexEntry> getEntries(IStrategoAppl template);

	/**
	 * Gets all child entries for URI in given template.
	 * 
	 * @param template	The template to match entries against.
	 */
	public abstract Collection<SemanticIndexEntry> getEntryChildTerms(IStrategoAppl template);

	/**
	 * Gets a (maximally shared) semantic index file for given file term.
	 * 
	 * @param fileTerm	A string or (string, string) tuple with the filename or the filename and subfilename.
	 */
	public abstract SemanticIndexFile getFile(IStrategoTerm fileTerm);
	
	/**
	 * Removes all entries in given file term and removes the file itself.
	 * 
	 * @param fileTerm	A string or (string, string) tuple with the filename or the filename and subfilename.
	 */
	public abstract void removeFile(IStrategoTerm fileTerm);

	/**
	 * Gets all files that are in the semantic index.
	 */
	public abstract Collection<SemanticIndexFile> getAllFiles();

	/**
	 * Clears the entire semantic index.
	 */
	public abstract void clear();

	/**
	 * Returns the semantic index as a stratego term.
	 * 
	 * @param includePositions
	 */
	public abstract IStrategoTerm toTerm(boolean includePositions);

	public abstract String toString();

}