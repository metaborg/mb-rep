package org.spoofax.interpreter.library.language.spxlang;

import jdbm.RecordListener;


/**
 * Record Listener for Compilation Unit.  
 *  
 * @author Md. Adil Akhter
 * Created On : Sep 5, 2011
 */
interface ICompilationUnitRecordListener
{
	public RecordListener<String, SpxCompilationUnitInfo> getCompilationUnitRecordListener();
}
