package org.spoofax.interpreter.library.language.spxlang.index;

import jdbm.RecordListener;
import org.spoofax.interpreter.library.language.spxlang.index.data.SpxCompilationUnitInfo;

interface ICompilationUnitRecordListener{
	public RecordListener<String, SpxCompilationUnitInfo> getCompilationUnitRecordListener();
}