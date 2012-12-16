//@author Richard Vogelij

package org.spoofax.terms.convert;

import java.util.HashMap;

/* 
 * A tool to convert .tbl files to other formats.
 */
public class Main {
	
	final static String _version 		= "0.1";
	final static String P_INPUTFILE     = "--i";
	final static String P_OUTPUTFILE    = "--o";
	final static String P_OUTPUTTYPE    = "--ot";
	
	final static String[] reqParams = {P_INPUTFILE, P_OUTPUTFILE, P_OUTPUTTYPE};
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		HashMap<String, String> params = new HashMap<String, String>();
		String key = "";
		for (int i = 0; i < args.length; i++)
		{
			if (i % 2 == 0)	
				key = args[i];
			else
				params.put(key, args[i]);
		}
		
		for (String p : reqParams)
		{
			if (!params.containsKey(p) || params.get(p).equals(""))
			{
				showArgs();
				System.err.println("");
				System.err.println("Missing required parameter: " + p);
				return;
			}
		}
		
		if (params.get(P_OUTPUTTYPE).equals("saf"))
			new Converter(params.get(P_INPUTFILE)).WriteTerm(TermFormats.SAF, params.get(P_OUTPUTFILE));
		else
			System.err.println("Output type: "+ params.get(P_OUTPUTTYPE) + " is not recognized");
	}
	
	private static void showArgs()
	{
		System.err.println("Term file converter v." + _version);
		System.err.println("usage: tblconverter.jar [--option0 value0 --option1 value1 ...]");
		System.err.println("");
		System.err.println("");
		System.err.println("Required:");
		System.err.println("\t"+P_INPUTFILE+" <file.tbl>\tInput file");
		System.err.println("\t"+P_OUTPUTFILE+" <file.tbl>\tOutput file");
		System.err.println("\t"+P_OUTPUTTYPE+" saf");
		System.err.println("");
	}
	
}

