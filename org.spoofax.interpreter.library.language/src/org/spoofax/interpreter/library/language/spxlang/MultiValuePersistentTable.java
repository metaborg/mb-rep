package org.spoofax.interpreter.library.language.spxlang;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.spoofax.interpreter.terms.IStrategoTerm;

import jdbm.PrimaryMap;
import jdbm.RecordManager;

/**
 * Generic MultiValue Symbol Table to store the symbols. 
 * Symbols are stored in memory and persisted
 * on the disk if committed via {@link SpxPersistenceManager}.
 * 
 * @author Md. Adil Akhter
 * Created On : Aug 22, 2011
 */
class MultiValuePersistentTable {

	final PrimaryMap<IStrategoTerm,ArrayList<SpxSymbol>> symbols;
	
	public MultiValuePersistentTable(String name, ISpxPersistenceManager manager){
		
		symbols = manager.loadHashMap(name);
	}
	
	/**
	 * removes all the entries from current map
	 * 
	 * @throws IOException 
	 */
	public void clear() throws IOException{
		symbols.clear();
	}

	
	/**
	 * Defines symbol in the current symbol table. Define does not replace  
	 * old symbol mapped using the key with the new one. It just adds the 
	 * new symbol at the end of the multivalue-list. 
	 * 
	 * @param key - The key that the symbol will be mapped to .
	 * @param symbol - The symbol to store. 
	 */
	public void define(SpxSymbol symbol){
		IStrategoTerm key = symbol.Id();
		
		if ( symbols.containsKey(key)){
			symbols.get(key).add(symbol);
		}else{
			ArrayList<SpxSymbol> values = new ArrayList<SpxSymbol>(); 
			values.add(symbol);
			symbols.put( key , values );
		}
	}
	
	public List<SpxSymbol> get(IStrategoTerm id){ return symbols.get(id); }
}
