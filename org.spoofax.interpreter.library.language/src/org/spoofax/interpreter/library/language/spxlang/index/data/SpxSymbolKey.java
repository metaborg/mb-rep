package org.spoofax.interpreter.library.language.spxlang.index.data;

import java.io.Serializable;

import org.spoofax.interpreter.terms.IStrategoTerm;

public class SpxSymbolKey extends SpxBaseSymbol implements Serializable{
	
	private static final long serialVersionUID = 7804281029276443583L;
	
	public SpxSymbolKey(IStrategoTerm id){ super(id); }

	@Override
	public String toString() {
		return "Key = [" + this.getId() +"]";
	}
}