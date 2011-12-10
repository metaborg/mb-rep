package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.Serializable;

import org.spoofax.interpreter.library.language.spxlang.index.Utils;
import org.spoofax.interpreter.terms.IStrategoConstructor;
import org.spoofax.interpreter.terms.IStrategoTerm;

public class SpxSymbolKey extends SpxBaseSymbol implements Serializable{
	
	private static final long serialVersionUID = 7804281029276443583L;

	public SpxSymbolKey(IStrategoTerm id, IStrategoConstructor signature){ this(id, signature, true); }
	
	public SpxSymbolKey(IStrategoTerm id, IStrategoConstructor signature, boolean isOveridable){ 
		super(id,signature,isOveridable);
	}

	@Override
	public String toString() {
		String keyStr = "Key = [" + this.getId() +"] ";
		if (isOverridable()){ 
			return "Overridable-"+keyStr; 
		}
		return keyStr;
	}
	
	public String printSymbolKey(){
		String toRet =  Utils.getCsvFormatted(this.getId().toString()) ;
		
		if(isOverridable()) {return toRet+"{overridable}" ;}
		return toRet;
	}
}