package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.IOException;

import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;
import org.spoofax.terms.attachments.TermAttachmentSerializer;


public final class SpxSymbolTableEntry
{
	public SpxSymbolKey key;
	public SpxSymbol value;
	
	public static final int SYMBOL_ID_INDEX = 1;
	public static final int TYPE_INDEX = 2;
	public static final int DATA_INDEX = 3;
	public static final int OVERRIDE_PROPERTY_INDEX = 4;
	
    public SpxSymbolTableEntry(SpxSymbolKey paramK, SpxSymbol paramV) {
      this.key = paramK;
      this.value = paramV;
    }
    
    public static SpxSymbolTableEntry.EntryBuilder newEntry() {return new SpxSymbolTableEntry.EntryBuilder();} 
    
    public static class EntryBuilder{
    	private IStrategoTerm _spoofaxkey ;
    	private IStrategoConstructor _signatureCtr ;
    	private IStrategoTerm _data;
    	private boolean _overridable = true;
    	// Instance of TermAttachmentSerializer to serialize terms
    	private TermAttachmentSerializer _serializer;
    	
    	public EntryBuilder with(IStrategoTerm id){ 
    		_spoofaxkey = id;
    		return this;
    	}
    	
    	public EntryBuilder instanceOf(IStrategoConstructor type){
    		_signatureCtr = type;
    		return this;
    	}
    	
    	public EntryBuilder data(IStrategoTerm data) {
    		_data = data;
    		return this;
    	}
    	
    	public EntryBuilder uses(TermAttachmentSerializer serializer){
    		_serializer = serializer;
    		return this;
    	}
    	
    	public EntryBuilder isUnique(){
    		_overridable = false;
    		return this;
    	}
    	
    	public SpxSymbolTableEntry build() throws IOException {
    		
    		SpxSymbolKey key = new SpxSymbolKey( _spoofaxkey , _signatureCtr, _overridable);
    		
    		SpxSymbol symbol = new SpxSymbol(_spoofaxkey,_signatureCtr,_overridable) ;
    		symbol.serializerDataString(_serializer, _data);
    		
    		return new SpxSymbolTableEntry(key , symbol );
		}
    }
}