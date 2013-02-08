package org.spoofax.interpreter.library.language.spxlang.index;

import jdbm.RecordListener;

import org.spoofax.interpreter.library.language.spxlang.index.data.ModuleDeclaration;
import org.spoofax.interpreter.terms.IStrategoList;

interface IModuleDeclarationRecordListener{
	
	public RecordListener<IStrategoList, ModuleDeclaration> getModuleDeclarationRecordListener();
}