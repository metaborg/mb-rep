package org.spoofax.interpreter.library.language.spxlang.index;

public final  class SpxIndexConfiguration {

	private SpxIndexConfiguration() {
	}
	
	public static final int NO_OF_ATTEMPT_TO_INIT_RECORDMANAGER = 1;
	private static boolean LOG_SYMBOLS = false;
	private static boolean DEBUG = false;
	private static String CSVDelimiter = " __ ";  
	
	
	public static String getCSVDelimiter() { return CSVDelimiter; }
	public static final String SPX_CACHE_DIRECTORY = ".spxcache";
	public static final String SPX_INDEX_DIRECTORY = ".spxindex";
	public static final String SPX_SHADOW_DIR = ".shadowdir";
	
	
	public static boolean shouldLogSymbols() {return LOG_SYMBOLS;}
	public static boolean shouldPrintDebugInfo() {return DEBUG;}
	
	
	
	public static void setLoggingSymbols(boolean shouldLogSymbolsAsCSV) {LOG_SYMBOLS = shouldLogSymbolsAsCSV;}
	public static void setTracing(boolean shouldWriteTraceInfo) { DEBUG  = shouldWriteTraceInfo;}
}
