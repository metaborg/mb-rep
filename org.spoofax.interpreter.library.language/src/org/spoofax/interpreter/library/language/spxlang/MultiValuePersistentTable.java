package org.spoofax.interpreter.library.language.spxlang;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.spoofax.interpreter.terms.IStrategoAppl;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoList;
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
public class MultiValuePersistentTable implements Serializable{

	private static final long serialVersionUID = -473055635199728599L;
	private final HashMap<SpxSymbolKey, List<SpxSymbol>> symbols;

	public MultiValuePersistentTable(){
		symbols = new HashMap<SpxSymbolKey , List<SpxSymbol>>();
	}
	
	/**
	 * Removes all the entries from this symbol-table
	 * 
	 * @throws IOException 
	 */
	public void clear(){ symbols.clear(); }

	
	/**
	 * Defines symbol in the current symbol table. Define does not replace  
	 * old symbol mapped using the key with the new one. It just adds the 
	 * new symbol at the end of the multivalue-list. 
	 * 
	 * @param key - The key that the symbol will be mapped to .
	 * @param symbol - The symbol to store. 
	 */
	public void define(SpxSymbolTableEntry entry){
		SpxSymbolKey key = entry.key;
		
		if ( symbols.containsKey(key)){
			symbols.get(key).add(entry.value);
		}else{
			List<SpxSymbol> values = new ArrayList<SpxSymbol>(); 
			values.add(entry.value);
			symbols.put( key , values );
		}
	}
	
	public List<SpxSymbol> resolve(IStrategoTerm id){
		
		SpxSymbolKey key = new SpxSymbolKey(id);
		List<SpxSymbol> resolvedSymbols = symbols.get(key);
		
		return (resolvedSymbols == null) ? new ArrayList<SpxSymbol>() : resolvedSymbols ; 
	}
	
	public void logEntries(BufferedWriter logger) throws IOException{
		for( SpxSymbolKey k : symbols.keySet()) {
			logger.write("\t"+k.toString()  + " :  \n");
			
			for( SpxSymbol s : symbols.get(k) ){
				logger.write( "\t\t"+ s.toString() + "\n");
			}
		}
		logger.write("\n");
	}
}
