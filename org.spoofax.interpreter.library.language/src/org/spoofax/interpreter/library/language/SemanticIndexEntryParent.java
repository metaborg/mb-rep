package org.spoofax.interpreter.library.language;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.TermFactory;

/**
 * @author Lennart Kats <lennart add lclnet.nl>
 */
public class SemanticIndexEntryParent extends SemanticIndexEntry {

	public static final IStrategoConstructor TYPE =
		new TermFactory().makeConstructor("<parent>", 1);

	private final Set<SemanticIndexEntry> children =
		new HashSet<SemanticIndexEntry>();
	
	private transient IStrategoList allDefsCached;
	
	protected SemanticIndexEntryParent(IStrategoTerm namespace, IStrategoList id) {
		super(TYPE, namespace, id, null, null);
	}
	
	@Override
	public IStrategoAppl toTerm(SemanticIndexEntryFactory factory) {
		return null;
	}
	
	@Override
	public void setTail(List<SemanticIndexEntry> tail) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addToTail(SemanticIndexEntry entry) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removeFromTail(SemanticIndexEntry entry) {
		throw new UnsupportedOperationException();
	}
	
	public void add(SemanticIndexEntry entry) {
		allDefsCached = null;
		children.add(entry);
	}
	
	public void remove(SemanticIndexEntry entry) {
		allDefsCached = null;
		children.remove(entry);
	}
	
	public boolean isEmpty() {
		return children.isEmpty();
	}
	
	public Set<SemanticIndexEntry> getChildren() {
		return Collections.unmodifiableSet(children);
	}
	
	public IStrategoList getAllDefsCached() {
		return allDefsCached;
	}
	
	public void setAllDefsCached(IStrategoList allDefsCached) {
		this.allDefsCached = allDefsCached;
	}
}
