package org.spoofax.interpreter.library.language.spxlang.index;

import jdbm.RecordListener;

import org.spoofax.interpreter.library.language.spxlang.index.data.PackageDeclaration;
import org.spoofax.interpreter.terms.IStrategoList;



interface IPackageDeclarationRecordListener
{
	public RecordListener<IStrategoList, PackageDeclaration> getPackageDeclarationRecordListener();
}