package org.spoofax.interpreter.library.language.spxlang;


import java.util.HashSet;
import java.util.Set;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

/**
 * BaseScope  is an abstract base class that implements Scope Interface
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 27, 2011
 */
abstract class BaseScope implements INamespace {
	
	private static final long serialVersionUID = 2052337390283813190L;
	
	protected final IStrategoConstructor type;
	protected final String  src;
	
	private  final NamespaceId _currentNamespace; 
	private final NamespaceId _enclosingNamespace;
	
	private final MultiValuePersistentTable _symbols;
	
	
	public BaseScope(NamespaceId currentNS , IStrategoConstructor type, NamespaceId enclosingNS, ISpxPersistenceManager manager) {
		assert currentNS!= null : "Current NS Identifier is null";
		assert type!= null      : "Non-null Type is expected";
		
		_currentNamespace = currentNS;
		_enclosingNamespace = enclosingNS; 
		
		this.type = type;
		
		src = type().toString() + _enclosingNamespace.UniqueID(); 
			
		_symbols = new MultiValuePersistentTable( manager.getProjectName()+src , manager);
	}

	public void define(SpxSymbol sym,ILogger logger){
		sym.setNamespace(_currentNamespace);
		
		logger.logMessage(src, "Defining Symbol "+ sym);
		
		_symbols.define(sym);
	}
		
	/* 
	 * Returns the enclosing scope of the current scope.
	 * */
	public NamespaceId getEnclosingNamespace() { return _enclosingNamespace; }
	
	public NamespaceId getCurrentNamespace(){ return _currentNamespace; }
	
	public Iterable<SpxSymbol> resolve(INamespaceResolver nsResolver, IStrategoTerm id, SearchPattern pattern,ILogger logger){
		Set<SpxSymbol> symbols = new HashSet<SpxSymbol>(); 
		
		
		return symbols;
	}
	
	public IStrategoConstructor type() {
		return type;
	}
}